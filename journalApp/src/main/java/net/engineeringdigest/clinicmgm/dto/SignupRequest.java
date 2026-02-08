package net.engineeringdigest.clinicmgm.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String username;
    private String password;
}
