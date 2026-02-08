package net.engineeringdigest.clinicmgm.service;

import net.engineeringdigest.clinicmgm.dto.LoginRequest;
import net.engineeringdigest.clinicmgm.dto.SignupRequest;
import net.engineeringdigest.clinicmgm.entity.Doctor;
import net.engineeringdigest.clinicmgm.repository.DoctorRepository;
import net.engineeringdigest.clinicmgm.utilis.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public void signup(SignupRequest request){
        if(doctorRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Doctor Already Exists");
        }
        Doctor doctor=new Doctor();
        doctor.setEmail(request.getEmail());
        doctor.setPassword(passwordEncoder.encode(request.getPassword()));
        doctor.setUsername(request.getUsername());
        doctorRepository.save(doctor);
    }

    public String login(LoginRequest request){
        Doctor doctor=doctorRepository.findByEmail(request.getEmail()).orElseThrow(()->new RuntimeException("Invalid Credentials"));
        System.out.println(doctor);
        if(!passwordEncoder.matches(request.getPassword(), doctor.getPassword())){
            throw new RuntimeException("Invalid Credentials");
        }

        return jwtUtil.generateToken(doctor.getEmail(),doctor.getId());
    }
}
