package net.engineeringdigest.clinicmgm.controller;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.clinicmgm.entity.Doctor;
import net.engineeringdigest.clinicmgm.entity.PatientToken;
import net.engineeringdigest.clinicmgm.repository.DoctorRepository;
import net.engineeringdigest.clinicmgm.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/doctor/queue")
@PreAuthorize("hasRole('DOCTOR')")
@Slf4j
public class DoctorQueueController {
    @Autowired
    private QueueService queueService;



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


}
