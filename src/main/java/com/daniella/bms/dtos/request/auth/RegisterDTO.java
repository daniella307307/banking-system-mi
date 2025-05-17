package com.daniella.bms.dtos.request.auth;

import com.daniella.bms.annotations.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDTO {
    @Schema(example = "John")
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @Schema(example = "Doe")
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @Schema(example = "example@gmail.com")
    @NotBlank(message = "Email cannot be blank")
    private String email;
    @Schema(example = "0788214246")
    @NotBlank(message = "Phone number can not be null")
    private String phoneNumber;
    @Schema(example = "1999-12-31")
    @NotNull(message = "Phone number cannot be null")
    private String dob;
    @Schema(example = "Password@123")
    @NotBlank(message = "Date of birth cannot be blank")
    @Size(min = 8,message = "Password must be at least 8 characters long")
    @ValidPassword(message = "Password should be strong")
    private String password;
}
