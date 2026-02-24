package net.engineeringdigest.clinicmgm.controller;

import net.engineeringdigest.clinicmgm.dto.PublicQueueResponse;
import net.engineeringdigest.clinicmgm.dto.QueueStatusResponse;
import net.engineeringdigest.clinicmgm.dto.QueueTokenRequestDto;
import net.engineeringdigest.clinicmgm.entity.PatientToken;
import net.engineeringdigest.clinicmgm.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/queue")
public class QueueController {
    @Autowired
    private QueueService queueService;


    @PostMapping("/token")
    public ResponseEntity<?> createToken(@RequestBody QueueTokenRequestDto request){
        PatientToken token = queueService.createToken(request.getQrKey(), request.getMobileNumber());

        Map<String,Object> response=new HashMap<>();
        response.put("tokenNumber",token.getTokenNumber());
        response.put("position",token.getPosition());
        response.put("estimatedArrivalMinutes",token.getEstimatedArrivalMinutes());

//        return new ResponseEntity<>("Response: "+response, HttpStatus.OK);
        return ResponseEntity.ok(response);
    }

   /* @GetMapping("/status/{tokenId}")
    public ResponseEntity<?> getQueueStatus(@PathVariable Long tokenId){
        QueueStatusResponse queueStatusForPatient = queueService.getQueueStatusForPatient(tokenId);

        return new ResponseEntity<>("Queue:"+"\n"+queueStatusForPatient,HttpStatus.OK);
    }*/

    @GetMapping("/status/{qrKey}")
    public ResponseEntity<List<PublicQueueResponse>> getPublicQueue(
            @PathVariable String qrKey) {

        List<PublicQueueResponse> queue =
                queueService.getPublicQueue(qrKey);

        return ResponseEntity.ok(queue);
    }




}
