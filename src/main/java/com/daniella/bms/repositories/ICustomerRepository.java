package com.daniella.bms.repositories;

import com.daniella.bms.models.Customer;
import com.daniella.bms.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ICustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByProfile_Email(String email);
    Optional<Customer> findByProfile(User profile);
}
