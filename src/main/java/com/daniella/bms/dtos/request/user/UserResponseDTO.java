package com.daniella.bms.dtos.request.user;

import com.daniella.bms.models.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDTO {
    private User user;
}
