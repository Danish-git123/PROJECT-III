package net.engineeringdigest.clinicmgm.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.clinicmgm.dto.PrescriptionRequest;
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

import java.util.Optional;

@Service
@Slf4j
public class PrescriptionService {
    @Autowired
    private PatientTokenRepository patientTokenRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private TwilioSmsService smsService;

    public void savePrescription(PrescriptionRequest req){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || authentication.getPrincipal()==null){
            throw new RuntimeException("Unauthenticated request");
        }

//        doctor id
        Long doctorId= (Long) authentication.getDetails();
        Optional<Doctor> doctor=doctorRepository.findById(doctorId);

        if(!doctor.isPresent()){
            throw new RuntimeException("Doctor Not Found");
        }

        PatientToken token =
                patientTokenRepository
                        .findByDoctorAndStatus(doctor.get(), "ONGOING")
                        .orElseThrow(() ->
                                new RuntimeException("No active patient")
                        );

        Prescription p = prescriptionRepository
                .findByToken(token)
                .orElse(new Prescription());

        p.setToken(token);
        p.setDoctor(token.getDoctor());
        p.setMobileNumber(token.getMobileNumber());

        p.setDiagnosis(req.getDiagnosis());
        p.setMedicines(req.getMedicines());
        p.setInstructions(req.getInstructions());

        prescriptionRepository.save(p);
    }

    public void sendPrescription(Long tokenId){
        Prescription p = prescriptionRepository
                .findById(tokenId)
                .orElseThrow(() ->
                        new RuntimeException("Prescription not filled")
                );

        if (p.isSent()) return;

        String message="Doctor Name: "+p.getDoctor().getUsername()+"\n"
                +"Prescription Id: "+p.getId()+"\n"
                +"Diagnosis: "+p.getDiagnosis()+"\n"
                +"Treatment: "+p.getMedicines()+"\n"
                +"Instructions: "+p.getInstructions();

        log.info(message);

        smsService.sendSms(
                p.getMobileNumber(),
                message
        );

        p.setSent(true);
        prescriptionRepository.save(p);
    }
}
