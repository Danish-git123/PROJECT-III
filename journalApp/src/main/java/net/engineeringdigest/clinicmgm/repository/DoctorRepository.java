package net.engineeringdigest.clinicmgm.repository;

import net.engineeringdigest.clinicmgm.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.Optional;


public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    Optional<Doctor>findByEmail(String email);
    Optional<Doctor>findByQrKey(String qrKey);
    Optional<Doctor>findByUsername(String username);
    boolean existsByEmail(String email);
//    Optional<Doctor>findById(Long id);
}
