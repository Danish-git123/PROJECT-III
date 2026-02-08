package net.engineeringdigest.clinicmgm.controller;


import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.clinicmgm.dto.ProfileCompletionDto;
import net.engineeringdigest.clinicmgm.entity.Doctor;
import net.engineeringdigest.clinicmgm.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/profile-completion")
public class ProfileCompletion {

    @Autowired
    private DoctorRepository doctorRepository;

    @PostMapping()
    public ResponseEntity<?> profileCompletionfunc(
            @RequestBody ProfileCompletionDto request) {

        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication instanceof AnonymousAuthenticationToken) {
                return new ResponseEntity<>("User is not authenticated ",HttpStatus.UNAUTHORIZED);

            }

            String email=authentication.getName();

            Doctor doctor = doctorRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));

            doctor.setWorkingStartTime(request.getWorkingStartTime());
            doctor.setWorkingEndTime(request.getWorkingEndTime());
            doctor.setConsultationAvgTime(request.getConsultationAvgTime());

            // 👇 endTime ke base pe auto calculate
            doctor.setTokenCutoffTime();
            doctor.updateAvailabilityByWorkingHours();

            if(doctor.getQrKey()==null){
                doctor.setQrKey(UUID.randomUUID().toString());
            }

            doctorRepository.save(doctor);

            return ResponseEntity.ok("Profile completed successfully");

        } catch (Exception e) {
            log.error("Failed in completing the profile", e);
            throw new RuntimeException(e);
        }
    }

}
