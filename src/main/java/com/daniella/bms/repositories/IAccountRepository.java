package com.daniella.bms.repositories;

import com.daniella.bms.models.Account;
import com.daniella.bms.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IAccountRepository extends JpaRepository<Account, UUID> {
    public boolean existsByOwner(Customer customer);
    public Optional<Account> findByOwner(Customer customer);
    public Optional<Account> findByAccountNumber(String accountNumber);
}
