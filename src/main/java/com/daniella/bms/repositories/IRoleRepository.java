package com.daniella.bms.repositories;

import com.daniella.bms.enums.ERole;
import com.daniella.bms.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findRoleByRoleName(ERole name);
    boolean existsByRoleName(String name);
}
