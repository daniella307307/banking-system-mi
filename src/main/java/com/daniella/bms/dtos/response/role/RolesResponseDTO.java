package com.daniella.bms.dtos.response.role;

import com.daniella.bms.models.Role;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.domain.Page;

@Data
public class RolesResponseDTO {
    @NonNull
    private Page<Role> roles;

    public RolesResponseDTO(@NonNull Page<Role> rolePage) {
        this.roles = rolePage;
    }
}