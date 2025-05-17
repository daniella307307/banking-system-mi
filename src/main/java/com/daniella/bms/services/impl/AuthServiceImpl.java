package com.daniella.bms.services.impl;

import com.daniella.bms.dtos.request.auth.LoginDTO;
import com.daniella.bms.dtos.response.auth.AuthResponse;
import com.daniella.bms.email.EmailService;
import com.daniella.bms.enums.IEmailTemplate;
import com.daniella.bms.models.User;
import com.daniella.bms.repositories.IUserRepositories;
import com.daniella.bms.security.jwt.JwtUtils;
import com.daniella.bms.security.user.UserPrincipal;
import com.daniella.bms.services.services.IAuthService;
import com.daniella.bms.services.services.IUserService;
import com.daniella.bms.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserRepositories userRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationProvider authenticationProvider;
    private final EmailService emailService;
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;

    private static final Duration CODE_VALIDITY_DURATION = Duration.ofMinutes(15);

    @Override
    public AuthResponse login(LoginDTO loginDTO) {
        Authentication authentication = authenticateUser(loginDTO);
        return generateJwtAuthenticationResponse(authentication);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new RuntimeException(String.format("User with email %s is not found", email))
        );
        String resetCode = UserUtils.generateToken();
        user.setPasswordResetCode(resetCode);
        user.setPasswordResetCodeGeneratedAt(LocalDateTime.now());
        userRepository.save(user);

        Map<String, Object> variables = new HashMap<>();
        variables.put("code", resetCode);

        sendTemplateEmail(user, "Reset Password", IEmailTemplate.RESET_PASSWORD, variables);
    }

    @Override
    public void resetPassword(String email, String passwordResetCode, String newPassword) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new RuntimeException("User with this email is not found")
        );

        if (!passwordResetCode.equals(user.getPasswordResetCode())) {
            throw new RuntimeException("Invalid password reset code");
        }

        if (user.getPasswordResetCodeGeneratedAt() == null ||
                user.getPasswordResetCodeGeneratedAt().isBefore(LocalDateTime.now().minus(CODE_VALIDITY_DURATION))) {
            throw new RuntimeException("Password reset code has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetCode(null);
        user.setPasswordResetCodeGeneratedAt(null);
        userRepository.save(user);
    }

    @Override
    public void initiateAccountVerification(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new RuntimeException("User with this email is not found")
        );

        if (user.isVerified()) return;

        String verificationCode = UserUtils.generateToken();
        user.setVerificationCode(Integer.parseInt(verificationCode));
        user.setVerificationCodeGeneratedAt(LocalDateTime.now());
        userRepository.save(user);

        Map<String, Object> variables = new HashMap<>();
        variables.put("code", verificationCode);

        sendTemplateEmail(user, "Verify your account", IEmailTemplate.ACCOUNT_VERIFICATION, variables);
    }

    @Override
    public void verifyAccount(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode).orElseThrow(
                () -> new RuntimeException("Invalid verification code")
        );

        if (user.getVerificationCodeGeneratedAt() == null ||
                user.getVerificationCodeGeneratedAt().isBefore(LocalDateTime.now().minus(CODE_VALIDITY_DURATION))) {
            throw new RuntimeException("Verification code has expired");
        }

        user.setVerified(true);
        user.setVerificationCode();
        user.setVerificationCodeGeneratedAt(null);
        userRepository.save(user);
    }

    @Override
    public void resendVerificationCode(String email) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        if (user.isVerified()) return;

        String newCode = UserUtils.generateToken();
        user.setVerificationCode(Integer.parseInt(newCode));
        user.setVerificationCodeGeneratedAt(LocalDateTime.now());
        userRepository.save(user);

        Map<String, Object> variables = new HashMap<>();
        variables.put("code", newCode);

        sendTemplateEmail(user, "Resend Account Verification", IEmailTemplate.ACCOUNT_VERIFICATION, variables);
    }

    @Override
    public void updatePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findUserByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // Helper method to authenticate a user
    public Authentication authenticateUser(LoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        Authentication authentication = authenticationProvider.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // Helper method to generate JWT response
    private AuthResponse generateJwtAuthenticationResponse(Authentication authentication) {
        String jwt = jwtUtils.generateAccessToken(authentication);
        UserPrincipal userPrincipal = UserUtils.getLoggedInUser();
        if (userPrincipal == null) {
            throw new RuntimeException("Failed to retrieve authenticated user");
        }

        User user = userService.findUserById(userPrincipal.getId());
        user.setFullName(user.getFirstname() + " " + user.getLastname());
        return new AuthResponse(jwt, user);
    }

    // Helper method to send templated emails
    private void sendTemplateEmail(User user, String subject, IEmailTemplate template, Map<String, Object> variables) {
        try {
            emailService.sendEmail(user.getEmail(), user.getFirstname(), subject, template, variables);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
