package com.daniella.bms.services.services;

import com.daniella.bms.dtos.request.auth.LoginDTO;
import com.daniella.bms.dtos.response.auth.AuthResponse;

public interface IAuthService {
    AuthResponse login(LoginDTO loginDTO);
    void forgotPassword(String email);
    void resetPassword(String email,String passwordResetCode,String newPassword);
    void initiateAccountVerification(String email);
    void verifyAccount(String verificationCode);
    void resendVerificationCode(String email);
    void updatePassword(String email, String oldPassword,String newPassword);
}
