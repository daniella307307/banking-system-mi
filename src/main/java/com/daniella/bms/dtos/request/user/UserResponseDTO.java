package com.daniella.bms.dtos.request.user;

import com.daniella.bms.enums.EAccountStatus;
import com.daniella.bms.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private User user;
    private String email;

    public UserResponseDTO(UUID id, String email, String firstname, String lastname, String phoneNumber, String fullName, LocalDate dob, EAccountStatus status, Set<UUID> collect) {
    }

    public UserResponseDTO(User user) {
        this.user=user;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
