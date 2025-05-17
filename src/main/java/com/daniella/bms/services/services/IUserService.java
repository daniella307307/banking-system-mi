package com.daniella.bms.services.services;

import com.daniella.bms.dtos.request.auth.UpdateUserDTO;
import com.daniella.bms.dtos.request.user.CreateAdminDTO;
import com.daniella.bms.dtos.request.user.UserResponseDTO;
import com.daniella.bms.dtos.request.user.UserRoleModificationDTO;
import com.daniella.bms.models.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    User findUserById(UUID userId);
    UserResponseDTO getLoggedInUser();
    UserResponseDTO createAdmin(CreateAdminDTO createAdminDTO);
    List<UserResponseDTO> getUsers();
    UserResponseDTO getUserById(UUID uuid);
    UserResponseDTO updateUser(UUID userId, UpdateUserDTO updateUserDTO);
    UserResponseDTO addRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO);

    UserResponseDTO removeRoles(UUID userId, UserRoleModificationDTO userRoleModificationDTO);

    void deleteUser(UUID userId);
}
