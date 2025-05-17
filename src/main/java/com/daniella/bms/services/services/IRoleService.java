package com.daniella.bms.services.services;

import com.daniella.bms.dtos.request.role.CreateRoleDTO;
import com.daniella.bms.dtos.response.role.RoleResponseDTO;
import com.daniella.bms.dtos.response.role.RolesResponseDTO;
import com.daniella.bms.enums.ERole;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IRoleService {
    RoleResponseDTO getRoleById(UUID roleId);
    RoleResponseDTO getRoleByName(ERole roleName);
    RoleResponseDTO createRole(CreateRoleDTO createRoleDTO);
    RolesResponseDTO getRoles(Pageable pageable);
    void deleteRole(UUID roleId);
    boolean isRolePresent(ERole roleName);
}