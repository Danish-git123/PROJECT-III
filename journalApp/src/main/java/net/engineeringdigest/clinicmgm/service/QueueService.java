package net.engineeringdigest.clinicmgm.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.clinicmgm.dto.QueueStatusResponse;
import net.engineeringdigest.clinicmgm.entity.Doctor;
import net.engineeringdigest.clinicmgm.entity.PatientToken;
import net.engineeringdigest.clinicmgm.entity.Prescription;
import net.engineeringdigest.clinicmgm.repository.DoctorRepository;
import net.engineeringdigest.clinicmgm.repository.PatientTokenRepository;
import net.engineeringdigest.clinicmgm.repository.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
//@Transactional
public class QueueService {
    @Autowired
    private PatientTokenRepository patientTokenRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private TwilioSmsService smsService;

    @Autowired
    private RedisQueueService redisQueueService;


    private Doctor getCurrentDoctor(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication==null || authentication.getPrincipal()==null){
            throw new RuntimeException("Unauthenticated request");
        }

        Long doctorId= (Long) authentication.getDetails();
        System.out.println(doctorId);
        return doctorRepository.findById(doctorId).orElseThrow(()->new RuntimeException("Doctor not found"));
    }



//    @Autowired
//    private SmsService smsService;

//    @Autowired
//    private PatientTokenRepositoryImpl patientTokenRepositoryImpl;

    public PatientToken createToken(String qrKey,String mobileNumber){
        Doctor doctor = doctorRepository.findByQrKey(qrKey).orElseThrow(() -> new RuntimeException("Invalid Qr Code"));

        LocalTime now = LocalTime.now();

        if (doctor.getWorkingStartTime() == null ||
                doctor.getTokenCutoffTime() == null ||
                now.isBefore(doctor.getWorkingStartTime()) ||
                now.isAfter(doctor.getTokenCutoffTime())) {


            throw new RuntimeException("Token generation not allowed at this time");
        }

        int position=doctor.getPatientInQueue()+1;
        int tokenNumber=position;

        int avgTime=doctor.getConsultationAvgTime();
        int estimatedArrival=((position-1)*avgTime+2);

        PatientToken token=new PatientToken();
        token.setDoctor(doctor);
        token.setTokenNumber(tokenNumber);
        token.setMobileNumber(mobileNumber);
        token.setPosition(position);
        token.setEstimatedArrivalMinutes(estimatedArrival);
        token.setStatus("ISSUED");

        patientTokenRepository.save(token);

        doctor.setPatientInQueue(position);
        doctorRepository.save(doctor);

        // 📩 SMS message
        String message =
                "Token Issued \n" +
                        "Doctor: " + doctor.getEmail() +"Doctor name: "+ doctor.getUsername() + "\n" +
                        "Token No: " + tokenNumber + "\n" +
                        "Position: " + position + "\n" +
                        "Estimated time: " + estimatedArrival + " minutes";

        log.info(message);

//  Send SMS (logs + console + Twilio)
        smsService.sendSms(mobileNumber, message);

        return token;
    }

    @Transactional
    public PatientToken callNext(){

        Doctor doctor=getCurrentDoctor();

        System.out.println(doctor.getId());
        System.out.println(doctor.getEmail());

        if(doctor.getStatus().equals("HOLD")){
            try {
                throw new Exception("Queue is in Hold Mode first release the Hold mode");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

// Finish ONGOING patient
        patientTokenRepository
                .findByDoctorAndStatus(doctor, "ONGOING")
                .ifPresent(current -> {
                    current.setStatus("COMPLETED");
                    current.setCallStartedAt(null);
                    patientTokenRepository.save(current);
                });


        PatientToken next = patientTokenRepository
                .findFirstByDoctorAndStatusOrderByPositionAsc(
                        doctor, "ISSUED"
                )
                .orElseThrow(() ->
                        new RuntimeException("No patients available"));



        next.setStatus("ONGOING");
        next.setCallStartedAt(LocalDateTime.now());
        next.setEstimatedArrivalMinutes(0);


        List<PatientToken> remainingPatients = patientTokenRepository.findAllByDoctorAndStatus(doctor, "ISSUED");
        int avgConsultationTime = doctor.getConsultationAvgTime();

        for (PatientToken patient : remainingPatients) {
            int updatedTime = patient.getEstimatedArrivalMinutes() - avgConsultationTime;

            // Math.max ensures ki time negative (minus) mein na chala jaye
            patient.setEstimatedArrivalMinutes(Math.max(0, updatedTime));
        }

        // Save all updated patients at once
        patientTokenRepository.saveAll(remainingPatients);
        return next;

    }

    // marking missed as patient
    @Transactional
    public void markMissed(Long tokenId) {
        Doctor doctor = getCurrentDoctor();


        PatientToken token = patientTokenRepository.findById(tokenId)
                .orElseThrow(() -> new RuntimeException("Token not found"));



        if (doctor.getId()!=(token.getDoctor().getId())) {
            throw new RuntimeException("Token does not belong to this doctor");
        }


        if (!"ONGOING".equals(token.getStatus())) {
            throw new RuntimeException("Only ONGOING token can be marked MISSED");
        }


        int currentPos = token.getPosition();


        Optional<PatientToken> anchorOpt =
                patientTokenRepository.findByDoctorAndPosition(
                        doctor,
                        currentPos + 2
                );


// free doctor
        token.setStatus("ISSUED");
        token.setCallStartedAt(null);


        if (anchorOpt.isPresent()) {
            int anchorPos = anchorOpt.get().getPosition();


            patientTokenRepository.shiftQueueUp(
                    doctor,
                    currentPos,
                    anchorPos
            );


            token.setPosition(anchorPos);
        } else {
            Integer lastPos =
                    patientTokenRepository.findLastPosition(doctor);
            token.setPosition(lastPos + 1);
        }


        patientTokenRepository.save(token);
    }

    public List<PatientToken> getQueue() {
        Doctor doctor=getCurrentDoctor();
        doctorRepository.findById(doctor.getId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));


        return patientTokenRepository
                .findByDoctorAndStatusInOrderByPositionAsc(
                        doctor,
                        new ArrayList<>(Arrays.asList("ISSUED"))
                );
    }


//    HOLD logic
//    @Transactional
    public void putDocOnHold(){
        Doctor doctor=getCurrentDoctor();

        if("HOLD".equals(doctor.getStatus())){
            return;
        }

        doctor.setStatus("HOLD");
        doctorRepository.save(doctor);
    }

    public void releseHold(){
        Doctor doctor=getCurrentDoctor();
        if(!doctor.getStatus().equals("HOLD")){
            return;
        }
        doctor.setStatus("AVAILABLE");
        doctorRepository.save(doctor);
    }

    public List<PatientToken> getQueueForDoctor(){
        Doctor doctor=getCurrentDoctor();

        return patientTokenRepository.findByDoctorAndStatusOrderByCreatedAtAsc(
                doctor,
                "ISSUED"
        );
    }

    public QueueStatusResponse getQueueStatusForPatient(Long tokenId){
        PatientToken token = patientTokenRepository.findById(tokenId).orElseThrow(() -> new RuntimeException("Invalid token"));

        if (!"ISSUED".equals(token.getStatus())) {
            throw new RuntimeException("Token not in waiting queue");
        }

        long peopleAhead = patientTokenRepository.countByDoctorAndStatusAndCreatedAtLessThan(
                token.getDoctor()
                , "ISSUED"
                , token.getCreatedAt()
        );

        return new QueueStatusResponse(
                token.getTokenNumber(),
                peopleAhead,
                token.getEstimatedArrivalMinutes()
        );
    }


}
