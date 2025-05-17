package com.daniella.bms.services.impl;

import com.daniella.bms.dtos.request.customer.CreateCustomerDTO;
import com.daniella.bms.dtos.request.customer.UpdateCustomerDTO;
import com.daniella.bms.dtos.response.customer.CustomerResponseDTO;
import com.daniella.bms.dtos.response.role.RoleResponseDTO;
import com.daniella.bms.enums.ERole;
import com.daniella.bms.models.Account;
import com.daniella.bms.models.Customer;
import com.daniella.bms.models.Role;
import com.daniella.bms.models.User;
import com.daniella.bms.repositories.IAccountRepository;
import com.daniella.bms.repositories.ICustomerRepository;
import com.daniella.bms.repositories.IUserRepositories;
import com.daniella.bms.services.services.ICustomerService;
import com.daniella.bms.services.services.IRoleService;
import com.daniella.bms.utils.Mapper;
import com.daniella.bms.utils.helpers.CustomerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor

public class CustomerServiceImpl implements ICustomerService {
    private final ICustomerRepository customerRepository;
    private final IUserRepositories userRepository;
    private final IAccountRepository accountRepository;
    private final IRoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final CustomerHelper customerHelper;

    @Override
    public CustomerResponseDTO createCustomer(CreateCustomerDTO dto) {
        if (customerRepository.existsByProfile_Email(dto.getEmail())) {
            throw new RuntimeException("The customer already exists");
        }

        try {
            RoleResponseDTO role = roleService.getRoleByName(ERole.USER);

            User user = customerHelper.buildUserFromDTO(dto, role, passwordEncoder);
            user = userRepository.save(user);

            Customer customer = customerHelper.buildCustomer(user);
            customer = customerRepository.save(customer);

            Account account = customerHelper.buildAccount(customer);
            account = accountRepository.save(account);

            customer.setAccount(account);
            customer = customerRepository.save(customer);

            CustomerResponseDTO response = Mapper.getMapper().map(user, CustomerResponseDTO.class);
            response.setId(customer.getId());
            response.setAccountNumber(account.getAccountNumber());
            response.setBalance(account.getBalance());

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create customer: " + e.getMessage());
        }
    }

    @Override
    public Customer getCustomerById(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));
    }

    @Override
    public Page<Customer> getAllCustomers(UUID customerId, UpdateCustomerDTO dto) {
        return null; // method stub retained as-is
    }

    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    public Customer updateCustomer(UUID customerId, UpdateCustomerDTO dto) {
        Customer customer = getCustomerById(customerId);
        User profile = customer.getProfile();

        profile.setFirstname(dto.getFirstName());
        profile.setLastname(dto.getLastName());
        // Removed invalid: profile.setFullName(...) — fullName is computed, not set
        profile.setDob(dto.getDob());

        userRepository.save(profile);
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(UUID customerId) {
        Customer customer = getCustomerById(customerId);

        accountRepository.delete(customer.getAccount());
        userRepository.delete(customer.getProfile());
        customerRepository.delete(customer);
    }
}
