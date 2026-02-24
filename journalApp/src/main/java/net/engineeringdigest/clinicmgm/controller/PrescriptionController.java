package net.engineeringdigest.clinicmgm.controller;

import net.engineeringdigest.clinicmgm.dto.PrescriptionRequest;
import net.engineeringdigest.clinicmgm.repository.DoctorRepository;
import net.engineeringdigest.clinicmgm.service.PrescriptionService;
import net.engineeringdigest.clinicmgm.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionController {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PrescriptionService prescriptionService;

    @PostMapping()
    public ResponseEntity<?> savePrescription(@RequestBody PrescriptionRequest request){
        prescriptionService.savePrescription(request);
        return new ResponseEntity<>("Sms gone to User", HttpStatus.OK);
    }

    @PostMapping("/send-prescription/{tokenId}")
    public ResponseEntity<?> sendPrescription(@PathVariable Long tokenId){
        prescriptionService.sendPrescription(tokenId);

        return ResponseEntity.ok("Prescription sent");
    }

}
