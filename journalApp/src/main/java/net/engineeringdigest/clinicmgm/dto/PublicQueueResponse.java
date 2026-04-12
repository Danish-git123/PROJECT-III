package net.engineeringdigest.clinicmgm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicQueueResponse {
    private Integer tokenNumber;
    private Integer position;
    private Integer estimatedArrivalMinutes;
    private String status;
    private boolean isQueuePaused;
}
