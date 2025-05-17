package com.daniella.bms.dtos.request.auth;

import lombok.Data;

@Data
public class LoginDTO {
    private String email;
    private String password;
}
