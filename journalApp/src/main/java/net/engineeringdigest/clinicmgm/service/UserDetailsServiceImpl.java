package net.engineeringdigest.clinicmgm.service;

import net.engineeringdigest.clinicmgm.entity.Doctor;
import net.engineeringdigest.clinicmgm.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private  DoctorRepository doctorRepository;

//    public UserDetailsServiceImpl(DoctorRepository doctorRepository) {
//        this.doctorRepository = doctorRepository;
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Doctor doctor=doctorRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Doctor not found"));

        return User.builder()
                .username(doctor.getEmail())
                .password(doctor.getPassword())
                .roles("DOCTOR")
                .build();
    }
}
