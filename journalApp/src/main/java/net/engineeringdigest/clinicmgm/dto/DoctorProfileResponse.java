package net.engineeringdigest.clinicmgm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor

public class DoctorProfileResponse {

    private Long id;
    private String email;
    private String username;
    private String status;
    private Integer checkedPatients;
    private Integer consultationAvgTime;
    private LocalTime workingStartTime;
    private LocalTime workingEndTime;
    private Integer patientInQueue;
    private String qr_key;
    private LocalDateTime createdAt;
}