package com.daniella.bms.dtos.response.role;

import com.daniella.bms.models.Role;
import org.springframework.data.domain.Page;

public class RolesResponseDTO {
    private Page<Role> roles;
}
