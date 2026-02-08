package net.engineeringdigest.clinicmgm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;

@Data
@Getter
@Setter
@NoArgsConstructor   // 🔥 REQUIRED
@AllArgsConstructor
public class ProfileCompletionDto {
    @JsonFormat(pattern = "HH:mm")
    private LocalTime workingStartTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime workingEndTime;
    private Integer consultationAvgTime;
}
