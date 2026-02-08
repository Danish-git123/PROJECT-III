package net.engineeringdigest.clinicmgm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "prescription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "token_id", unique = true, nullable = false)
    private PatientToken token;
    @ManyToOne
    private Doctor doctor;
    // Patient identity (lightweight, no Patient table)
    @Column(nullable = false, length = 15)
    private String mobileNumber;
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    @Column(columnDefinition = "TEXT")
    private String medicines;
    @Column(columnDefinition = "TEXT")
    private String instructions;
    private boolean sent;
}
