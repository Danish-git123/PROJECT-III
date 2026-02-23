package net.engineeringdigest.clinicmgm.controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.clinicmgm.dto.DoctorProfileResponse;
import net.engineeringdigest.clinicmgm.entity.Doctor;
import net.engineeringdigest.clinicmgm.entity.PatientToken;
import net.engineeringdigest.clinicmgm.repository.DoctorRepository;
import net.engineeringdigest.clinicmgm.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctor/queue")
@PreAuthorize("hasRole('DOCTOR')")
@Slf4j
public class DoctorQueueController {
    @Autowired
    private QueueService queueService;

    @Autowired
    private DoctorRepository doctorRepository;

    @PostMapping("/next")
    public ResponseEntity<Map<String, Object>> callNext() {

        Map<String, Object> response = new HashMap<>();

            PatientToken token = queueService.callNext();


            response.put("tokenId", token.getId());
            response.put("tokenNumber", token.getTokenNumber());
            response.put("status", token.getStatus());
            response.put("callStartedAt", token.getCallStartedAt());
        return ResponseEntity.ok(response);
    }

    // Doctor marks MISSED
    @PostMapping("/missed/{tokenId}")
    public ResponseEntity<?> markMissed(@PathVariable Long tokenId) {

        queueService.markMissed(tokenId);
        return ResponseEntity.ok("Patient marked MISSED");
    }

    @PutMapping("/hold")
    public ResponseEntity<?>Hold(){
        queueService.putDocOnHold();
        return new ResponseEntity<>("Doctor is on Hold", HttpStatus.OK);
    }

    @PutMapping("/release-hold")
    public ResponseEntity<?>releaseHold(){
        queueService.releseHold();
        return new ResponseEntity<>("Doctor is Available", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PatientToken>> getDoctorQueue() {

        List<PatientToken> queue = queueService.getQueueForDoctor();
        return ResponseEntity.ok(queue);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        String email = authentication.getName();

        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Update dynamic status before sending
        doctor.updateAvailabilityByWorkingHours();

        DoctorProfileResponse response = new DoctorProfileResponse(
                doctor.getId(),
                doctor.getEmail(),
                doctor.getUsername(),
                doctor.getStatus(),
                doctor.getCheckedPatients(),
                doctor.getConsultationAvgTime(),
                doctor.getWorkingStartTime(),
                doctor.getWorkingEndTime(),
                doctor.getPatientInQueue(),
                doctor.getQrKey(),
                doctor.getCreatedAt()
        );

        return ResponseEntity.ok(response);
    }

}
