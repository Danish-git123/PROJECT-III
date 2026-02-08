package net.engineeringdigest.clinicmgm.dto;

import lombok.Data;

@Data
public class PrescriptionRequest {

    private String diagnosis;
    private String medicines;
    private String instructions;
}
