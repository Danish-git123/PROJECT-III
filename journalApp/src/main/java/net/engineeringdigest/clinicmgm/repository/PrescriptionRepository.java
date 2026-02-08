package net.engineeringdigest.clinicmgm.repository;

import net.engineeringdigest.clinicmgm.entity.PatientToken;
import net.engineeringdigest.clinicmgm.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrescriptionRepository extends JpaRepository<Prescription,Long> {
    Optional<Prescription>findByToken(PatientToken token);
}
