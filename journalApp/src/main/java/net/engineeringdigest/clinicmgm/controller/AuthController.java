package net.engineeringdigest.clinicmgm.controller;

import net.engineeringdigest.clinicmgm.dto.AuthResponse;
import net.engineeringdigest.clinicmgm.dto.LoginRequest;
import net.engineeringdigest.clinicmgm.dto.SignupRequest;
import net.engineeringdigest.clinicmgm.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?>signup(@RequestBody SignupRequest request){
        authService.signup(request);
        return new ResponseEntity<>("Doctor Registered Successfully",HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody LoginRequest request){
        String token=authService.login(request);
//        return new ResponseEntity<>("Token: "+token,HttpStatus.OK);
        return ResponseEntity.ok(new AuthResponse(token));


    }
}
