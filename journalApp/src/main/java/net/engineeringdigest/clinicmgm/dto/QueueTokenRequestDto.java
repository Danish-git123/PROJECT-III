package net.engineeringdigest.clinicmgm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueTokenRequestDto {
    private String qrKey; // mandatory
    private String mobileNumber; // mandatory
}
