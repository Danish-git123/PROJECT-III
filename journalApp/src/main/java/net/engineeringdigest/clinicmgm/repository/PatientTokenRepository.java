package net.engineeringdigest.clinicmgm.repository;

import net.engineeringdigest.clinicmgm.entity.Doctor;
import net.engineeringdigest.clinicmgm.entity.PatientToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PatientTokenRepository extends JpaRepository<PatientToken,Long> {



    //  Currently CALLED token
    Optional<PatientToken> findByDoctorAndStatus(
            Doctor doctor,
            String status
    );

    // PatientTokenRepository.java mein
    List<PatientToken> findAllByDoctorAndStatus(Doctor doctor, String status);


    //  FIFO: next patient to call
    Optional<PatientToken>
    findFirstByDoctorAndStatusOrderByPositionAsc(
            Doctor doctor,
            String status
    );


    //  Queue view for polling
    List<PatientToken>
    findByDoctorAndStatusInOrderByPositionAsc(
            Doctor doctor,
            List<String> statuses
    );


    //  Optional helpers
    int countByDoctorAndStatus(
            Doctor doctor,
            String status
    );

    @Query(
            "SELECT t FROM PatientToken t " +
                    "WHERE t.doctor = :doctor " +
                    "AND t.status = 'MISSED' " +
                    "AND t.retryAfter <= :completedCount " +
                    "ORDER BY t.tokenNumber ASC"
    )
    Optional<PatientToken> findFirstEligibleMissed(
            @Param("doctor") Doctor doctor,
            @Param("completedCount") long completedCount
    );

    Optional<PatientToken> findByDoctorAndPosition(
            Doctor doctor,
            Integer position
    );

    @Query("SELECT MAX(t.position) FROM PatientToken t WHERE t.doctor = :doctor")
    Integer findLastPosition(@Param("doctor") Doctor doctor);


    @Modifying
    @Query(
            "UPDATE PatientToken t " +
                    "SET t.position = t.position - 1 " +
                    "WHERE t.doctor = :doctor " +
                    "AND t.position > :from " +
                    "AND t.position <= :to"
    )
    void shiftQueueUp(
            @Param("doctor") Doctor doctor,
            @Param("from") Integer from,
            @Param("to") Integer to
    );

    /*Optional<PatientToken>
    findFirstByDoctorAndStatusAndRetryAfterLessThanEqualOrderByTokenNumberAsc(
            Doctor doctor,
            String status,
            Integer retryAfter
    );*/

    List<PatientToken> findByDoctorAndStatusOrderByCreatedAtAsc(
            Doctor doctor,
            String status
    );

    long countByDoctorAndStatusAndCreatedAtLessThan(
            Doctor doctor,
            String status,
            LocalDateTime createdAt
    );

    int countByDoctorAndStatusIn(Doctor doctor, List<String> statuses);


    List<PatientToken> findByDoctorOrderByTokenNumberAsc(Doctor doctor);
}
