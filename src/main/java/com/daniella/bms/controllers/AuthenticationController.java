package com.daniella.bms.controllers;

import com.daniella.bms.dtos.request.auth.LoginDTO;
import com.daniella.bms.dtos.request.auth.PasswordResetDTO;
import com.daniella.bms.dtos.request.auth.PasswordUpdateDTO;
import com.daniella.bms.dtos.request.user.CreateAdminDTO;
import com.daniella.bms.dtos.request.user.UserResponseDTO;
import com.daniella.bms.dtos.response.auth.AuthResponse;
import com.daniella.bms.dtos.response.payload.ApiResponse;
import com.daniella.bms.services.services.IAuthService;
import com.daniella.bms.services.services.IUserService;
import com.daniella.bms.utils.ExceptionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication",description = "Authentication and user management endpoint")
public class AuthenticationController {
    private final IAuthService authService;
    private final IUserService userService;
    private static final Logger logger= LoggerFactory.getLogger(AuthenticationController.class);
    @Operation(summary = "user login",description = "Authenticate a user and returns access token")
    @ApiResponses(
            value = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully authenticated"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginDTO loginDTO){
        try{
            logger.debug("Processing login request for user{}",loginDTO.getEmail());
            AuthResponse response=authService.login(loginDTO);
            return ApiResponse.success("Login successful", HttpStatus.OK,response);
        }catch (Exception e){
            logger.error("Login failed for user: {}", loginDTO.getEmail(),e);
            return ApiResponse.fail("Login failed",HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
    @Operation(summary = "Create admin account", description = "Creates a new administrator account")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createAdmin(@Valid @RequestBody CreateAdminDTO createAdminDTO) {
        try {
            logger.debug("Creating admin account for: {}", createAdminDTO.getEmail());
            UserResponseDTO createdUser = userService.createAdmin(createAdminDTO);
            return ApiResponse.success("Admin created successfully", HttpStatus.CREATED, createdUser);
        } catch (Exception e) {
            logger.error("Admin creation failed for: {}", createAdminDTO.getEmail(), e);
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Failed to create admin", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Initiate password reset", description = "Sends password reset instructions to email")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(
            @Parameter(description = "User's email address")
            @RequestParam @Email String email) {
        try {
            logger.debug("Processing password reset request for: {}", email);
            authService.forgotPassword(email);
            return ApiResponse.success("Reset password instructions sent to email", HttpStatus.OK, null);
        } catch (Exception e) {
            logger.error("Password reset initiation failed for: {}", email, e);
            return ApiResponse.fail("Failed to initiate password reset", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Reset password", description = "Resets user password using reset code")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody PasswordResetDTO dto) {
        try {
            logger.debug("Processing password reset for: {}", dto.getEmail());
            authService.resetPassword(dto.getEmail(), dto.getResetCode(), dto.getNewPassword());
            return ApiResponse.success("Password reset successful", HttpStatus.OK, null);
        } catch (Exception e) {
            logger.error("Password reset failed for: {}", dto.getEmail(), e);
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Failed to reset password", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Initiate account verification", description = "Sends verification code to user's email")
    @PostMapping("/initiate-verification")
    public ResponseEntity<ApiResponse<Object>> initiateAccountVerification(
            @Parameter(description = "User's email address")
            @RequestParam @Email String email) {
        try {
            logger.debug("Initiating account verification for: {}", email);
            authService.initiateAccountVerification(email);
            return ApiResponse.success("Account verification initiated", HttpStatus.OK, null);
        } catch (Exception e) {
            logger.error("Account verification initiation failed for: {}", email, e);
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Account verification failed", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Verify account", description = "Verifies user account with verification code")
    @PostMapping("/verify-account")
    public ResponseEntity<ApiResponse<Object>> verifyAccount(
            @Parameter(description = "Verification code")
            @RequestParam String code) {
        try {
            logger.debug("Processing account verification with code: {}", code);
            authService.verifyAccount(code);
            return ApiResponse.success("Account verified successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            logger.error("Account verification failed for code: {}", code, e);
            ExceptionUtils.handleResponseException(e);
            return ApiResponse.fail("Account verification failed", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Resend verification code", description = "Resends account verification code to email")
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Object>> resendVerificationCode(
            @Parameter(description = "User's email address")
            @RequestParam @Email String email) {
        try {
            logger.debug("Resending verification code to: {}", email);
            authService.resendVerificationCode(email);
            return ApiResponse.success("Verification code resent successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            logger.error("Failed to resend verification code to: {}", email, e);
            return ApiResponse.fail("Failed to resend verification code", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Update password", description = "Updates password for authenticated user")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<Object>> updatePassword(@Valid @RequestBody PasswordUpdateDTO dto) {
        try {
            String email = userService.getLoggedInUser().getUser().getEmail();
            logger.debug("Processing password update for user: {}", email);
            authService.updatePassword(email, dto.getOldPassword(), dto.getNewPassword());
            return ApiResponse.success("Password updated successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            logger.error("Password update failed", e);
            return ApiResponse.fail("Failed to update password", HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
