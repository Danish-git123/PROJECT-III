package net.engineeringdigest.clinicmgm.entity;

import com.sun.xml.internal.bind.v2.model.core.ID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "doctor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true,nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(name = "username")
    private String username;
    private String status;//dynamic
    private Integer checkedPatients=0;//dynamic
    private  Integer consultationAvgTime=15;
    @Column(name = "qr_key", unique = true)
    private String qrKey;
    private LocalTime workingStartTime;
    private LocalTime workingEndTime;
    private LocalTime tokenCutoffTime;//before 30 mins of EndTime
    @Column(name = "patientInQueue")
    private Integer patientInQueue=0;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void setTokenCutoffTime() {
        if (this.workingEndTime == null) {
            throw new IllegalStateException("Working end time must be set first");
        }
        this.tokenCutoffTime = this.workingEndTime.minusMinutes(30);
        //working end time se 30 min paile token generate hona band ho jayenge
    }

    public void updateAvailabilityByWorkingHours() {

        // manual override respected
        if ("HOLD".equals(this.status)) {
            return;
        }

        LocalTime now = LocalTime.now();

        boolean withinWorkingHours =
                !now.isBefore(workingStartTime) &&
                        !now.isAfter(workingEndTime);

        this.status = withinWorkingHours ? "AVAILABLE" : "OFFLINE";
    }


}
