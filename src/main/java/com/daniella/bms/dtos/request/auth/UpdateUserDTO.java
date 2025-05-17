package com.daniella.bms.dtos.request.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserDTO {
    @Schema(example = "John")
    private String firstName;
    @Schema(example = "Doe")
    private String lastName;
    @Schema(example = "0788214246")
    private String phoneNumber;
    @Schema(example = "example@gmail.com")
    @Email(message = "Invalid email format")
    private String email;
}
