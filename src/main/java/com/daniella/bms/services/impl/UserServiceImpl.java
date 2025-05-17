package com.daniella.bms.services.impl;

import com.daniella.bms.dtos.request.auth.UpdateUserDTO;
import com.daniella.bms.dtos.request.user.CreateAdminDTO;
import com.daniella.bms.dtos.request.user.UserResponseDTO;
import com.daniella.bms.dtos.request.user.UserRoleModificationDTO;
import com.daniella.bms.enums.ERole;
import com.daniella.bms.models.Role;
import com.daniella.bms.models.User;
import com.daniella.bms.repositories.IRoleRepository;
import com.daniella.bms.repositories.IUserRepositories;
import com.daniella.bms.services.services.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final IUserRepositories userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${create.code}")
    private String adminCreateCode;

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InvalidAdminCodeException extends RuntimeException {
        public InvalidAdminCodeException(String message) {
            super(message);
        }
    }

    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException(String message) {
            super(message);
        }
    }

    public boolean isUserPresent(String email) {
        logger.debug("Checking if user exists with email: {}", email);
        return userRepository.findUserByEmail(email).isPresent();
    }

    @Override
    public User findUserById(UUID userId) {
        logger.debug("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    @Override
    public UserResponseDTO getLoggedInUser() {
        logger.debug("Fetching logged-in user");
        String username;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + username));
        return new UserResponseDTO(user);
    }

    public User createUserEntity(@Valid CreateAdminDTO createAdminDTO) {
        logger.info("Creating user entity for email: {}", createAdminDTO.getEmail());
        if (isUserPresent(createAdminDTO.getEmail())) {
            logger.warn("User with email {} already exists", createAdminDTO.getEmail());
            throw new UserAlreadyExistsException("User with email " + createAdminDTO.getEmail() + " already exists");
        }

        Role adminRole = roleRepository.findRoleByRoleName(ERole.ADMIN)
                .orElseThrow(() -> new RoleNotFoundException("ADMIN role not found"));

        return User.builder()
                .email(createAdminDTO.getEmail())
                .firstname(createAdminDTO.getFirstName())
                .lastname(createAdminDTO.getLastName())
                .dob(LocalDate.parse(createAdminDTO.getDob()))
                .phoneNumber(createAdminDTO.getPhoneNumber())
                .password(passwordEncoder.encode(createAdminDTO.getPassword()))
                .roles(new HashSet<>(Collections.singletonList(adminRole)))
                .build();
    }

    @Transactional
    @Override
    public UserResponseDTO createAdmin(@Valid CreateAdminDTO createAdminDTO) {
        logger.info("Creating admin with email: {}", createAdminDTO.getEmail());
        if (!createAdminDTO.getAdminCreateCode().equals(adminCreateCode)) {
            logger.warn("Invalid admin creation code");
            throw new InvalidAdminCodeException("Invalid admin creation code");
        }

        User user = createUserEntity(createAdminDTO);
        userRepository.save(user);
        logger.info("Admin created successfully: {}", user.getEmail());
        return new UserResponseDTO(user);
    }

    @Override
    public List<UserResponseDTO> getUsers() {
        logger.info("Fetching all users");

        return userRepository.findAll().stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstname(),
                        user.getLastname(),
                        user.getPhoneNumber(),
                        user.getFullName(),
                        user.getDob(),
                        user.getStatus(),
                        user.getRoles().stream()
                                .map(Role::getRoleId)
                                .collect(Collectors.toSet())
                ))
                .collect(Collectors.toList());
    }


    @Override
    public UserResponseDTO getUserById(UUID userId) {
        logger.debug("Fetching user by ID: {}", userId);
        User user = findUserById(userId);
        return new UserResponseDTO(
               user
        );
    }

    @Transactional
    @Override
    public UserResponseDTO updateUser(UUID userId, @Valid UpdateUserDTO updateUserDTO) {
        logger.info("Updating user with ID: {}", userId);
        User user = findUserById(userId);
        if (!user.getEmail().equals(updateUserDTO.getEmail()) && isUserPresent(updateUserDTO.getEmail())) {
            logger.warn("Email {} is already taken", updateUserDTO.getEmail());
            throw new UserAlreadyExistsException("Email " + updateUserDTO.getEmail() + " is already taken");
        }

        user.setFirstname(updateUserDTO.getFirstName());
        user.setLastname(updateUserDTO.getLastName());
        user.setPhoneNumber(updateUserDTO.getPhoneNumber());
        user.setEmail(updateUserDTO.getEmail());

        userRepository.save(user);
        logger.info("User updated successfully: {}", user.getEmail());
        return new UserResponseDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO addRoles(UUID userId, @Valid UserRoleModificationDTO userRoleModificationDTO) {
        logger.info("Adding roles to user with ID: {}", userId);
        User user = findUserById(userId);
        Set<Role> roles = user.getRoles();
        for (UUID roleId : userRoleModificationDTO.getRoles()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RoleNotFoundException("Role with ID " + roleId + " not found"));
            roles.add(role);
        }
        userRepository.save(user);
        logger.info("Roles added to user: {}", user.getEmail());
        return new UserResponseDTO(user);
    }

    @Transactional
    @Override
    public UserResponseDTO removeRoles(UUID userId, @Valid UserRoleModificationDTO userRoleModificationDTO) {
        logger.info("Removing roles from user with ID: {}", userId);
        User user = findUserById(userId);
        Set<Role> roles = user.getRoles();
        for (UUID roleId : userRoleModificationDTO.getRoles()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RoleNotFoundException("Role with ID " + roleId + " not found"));
            roles.remove(role);
        }
        userRepository.save(user);
        logger.info("Roles removed from user: {}", user.getEmail());
        return new UserResponseDTO(user);
    }

    @Transactional
    @Override
    public void deleteUser(UUID userId) {
        logger.info("Deleting user with ID: {}", userId);
        if (!userRepository.existsById(userId)) {
            logger.warn("User with ID {} not found", userId);
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
        logger.info("User deleted successfully: {}", userId);
    }
}