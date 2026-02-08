package net.engineeringdigest.clinicmgm.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    @GetMapping("/health-check")
    public ResponseEntity<?> healthCheck(){
        return new ResponseEntity<>("Health Check is fine ",HttpStatus.OK);
    }
}
