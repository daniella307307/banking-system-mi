package com.daniella.bms.services.impl;

import com.daniella.bms.dtos.request.role.CreateRoleDTO;
import com.daniella.bms.dtos.response.role.RoleResponseDTO;
import com.daniella.bms.dtos.response.role.RolesResponseDTO;
import com.daniella.bms.enums.ERole;
import com.daniella.bms.models.Role;
import com.daniella.bms.repositories.IRoleRepository;
import com.daniella.bms.services.services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements IRoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    private final IRoleRepository roleRepository;

    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException(String message) {
            super(message);
        }
    }

    public static class RoleAlreadyExistsException extends RuntimeException {
        public RoleAlreadyExistsException(String message) {
            super(message);
        }
    }

    @Override
    public RoleResponseDTO getRoleById(UUID roleId) {
        logger.debug("Fetching role by ID: {}", roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role with ID " + roleId + " not found"));
        return new RoleResponseDTO(role.getRoleId(), role.getRoleName());
    }

    @Override
    public RoleResponseDTO getRoleByName(ERole roleName) {
        logger.debug("Fetching role by name: {}", roleName);
        Role role = roleRepository.findRoleByRoleName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role " + roleName + " not found"));
        return new RoleResponseDTO(role.getRoleId(), role.getRoleName());
    }

    @Transactional
    @Override
    public RoleResponseDTO createRole(CreateRoleDTO createRoleDTO) {
        logger.info("Creating role: {}", createRoleDTO.getName());
        ERole roleName = createRoleDTO.getName();

        if (roleRepository.findRoleByRoleName(roleName).isPresent()) {
            logger.warn("Role {} already exists", roleName);
            throw new RoleAlreadyExistsException("Role " + roleName + " already exists");
        }

        Role role = Role.builder()
                .roleName(roleName)
                .build();

        Role savedRole = roleRepository.save(role);
        logger.info("Role {} created successfully", roleName);
        return new RoleResponseDTO(savedRole.getRoleId(), savedRole.getRoleName());
    }

    @Override
    public RolesResponseDTO getRoles(Pageable pageable) {
        logger.debug("Fetching roles with pageable: {}", pageable);
        if (pageable.getPageSize() > 100) {
            pageable = PageRequest.of(pageable.getPageNumber(), 100, pageable.getSort());
        }
        Page<Role> rolePage = roleRepository.findAll(pageable);
        return new RolesResponseDTO(rolePage);
    }

    @Transactional
    @Override
    public void deleteRole(UUID roleId) {
        logger.info("Deleting role with ID: {}", roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role with ID " + roleId + " not found"));
        roleRepository.delete(role);
        logger.info("Role with ID {} deleted successfully", roleId);
    }

    @Override
    public boolean isRolePresent(ERole roleName) {
        logger.debug("Checking if role {} exists", roleName);
        return roleRepository.findRoleByRoleName(roleName).isPresent();
    }
}