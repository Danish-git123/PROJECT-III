package net.engineeringdigest.clinicmgm.dto;

import lombok.Data;

@Data
public class QueueStatusResponse {
    private Integer tokenNumber;
    private long peopleAhead;
    private Integer estimatedArrivalMinutes;

    public QueueStatusResponse(Integer tokenNumber,
                               long peopleAhead,
                               Integer estimatedArrivalMinutes) {
        this.tokenNumber = tokenNumber;
        this.peopleAhead = peopleAhead;
        this.estimatedArrivalMinutes = estimatedArrivalMinutes;
    }
}
