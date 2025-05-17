package com.daniella.bms.repositories;

import com.daniella.bms.models.Role;
import com.daniella.bms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepositories extends JpaRepository<User, UUID> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findByRoles(Role role);
    Optional<User> findByVerificationCode(String verificationCode);
}
