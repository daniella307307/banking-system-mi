package com.daniella.bms.dtos.request.auth;

import com.daniella.bms.annotations.ValidPassword;
import lombok.Data;

@Data
public class PasswordUpdateDTO {
    private String oldPassword;
    @ValidPassword(message = "Password should be strong")
    private String newPassword;
}
