package net.engineeringdigest.clinicmgm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Token number shown to patient (1,2,3...)
    @Column(nullable = false)
    private Integer tokenNumber;


    // Patient mobile number (no patient profile)
    @Column(nullable = false, length = 15)
    private String mobileNumber;

    // Position in queue at time of creation
    @Column(nullable = false)
    private Integer position;


    // Estimated arrival time in minutes
    @Column(nullable = false)
    private Integer estimatedArrivalMinutes;


    // Token status
    @Column(nullable = false)
    private String status; // ISSUED, ONGOING , HOLD, MISSED, COMPLETED


    // Doctor reference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column
    private Long retryAfter;


    private LocalDateTime callStartedAt;

    private LocalDateTime createdAt = LocalDateTime.now();
}
