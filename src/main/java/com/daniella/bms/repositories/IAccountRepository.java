package com.daniella.bms.repositories;

import com.daniella.bms.models.Account;
import com.daniella.bms.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IAccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByOwner(Customer customer);
    Optional<Account> findByOwner(Customer customer);
    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findByEmail(String email); // if 'email' exists in Account
    Optional<Account> findByUserEmail(String email); // if 'user.email' is queryable
}
