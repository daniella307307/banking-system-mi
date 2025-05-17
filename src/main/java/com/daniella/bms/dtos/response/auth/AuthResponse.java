package com.daniella.bms.dtos.response.auth;

import com.daniella.bms.models.User;
import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private User user;
    public AuthResponse(String token,User user){
        this.token=token;
        this.user=user;
    }
}
