package net.engineeringdigest.clinicmgm.controller;

import net.engineeringdigest.clinicmgm.entity.Doctor;
import net.engineeringdigest.clinicmgm.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
public class DoctorQrController {
    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("qr-key")
    public ResponseEntity<?> getQrKey(){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();


        String email = authentication.getName();


        Doctor doctor = doctorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));


        if (doctor.getQrKey() == null) {
            return ResponseEntity.badRequest()
                    .body("Profile not completed yet");
        }

        Map<String, String> response = new HashMap<>();
        response.put("qrKey", doctor.getQrKey());
        response.put("qrUrl", "https://your-app.com/queue/join/" + doctor.getQrKey());

        return new ResponseEntity<>("Response: "+response,HttpStatus.OK);
    }
}
