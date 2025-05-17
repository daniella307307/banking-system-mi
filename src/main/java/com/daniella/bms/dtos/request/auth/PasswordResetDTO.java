package com.daniella.bms.dtos.request.auth;

import com.daniella.bms.annotations.ValidPassword;
import lombok.Data;

@Data
public class PasswordResetDTO {
    private String email;
    private String resetCode;
    @ValidPassword(message = "password should be strong")
    private String newPassword;
}
