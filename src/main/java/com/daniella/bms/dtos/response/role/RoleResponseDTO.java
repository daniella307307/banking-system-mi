package com.daniella.bms.dtos.response.role;

import com.daniella.bms.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RoleResponseDTO {
    private ERole roleName;
    private UUID roleId;

    public RoleResponseDTO(UUID roleId, ERole roleName) {
     this.roleId=roleId;
     this.roleName=roleName;
    }
}
