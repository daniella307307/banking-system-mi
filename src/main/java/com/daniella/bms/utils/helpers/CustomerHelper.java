package com.daniella.bms.utils.helpers;
import com.daniella.bms.dtos.request.customer.CreateCustomerDTO;
import com.daniella.bms.dtos.response.role.RoleResponseDTO;
import com.daniella.bms.models.Account;
import com.daniella.bms.models.Customer;
import com.daniella.bms.models.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;
import java.util.Set;

@Component
public class CustomerHelper {

    public User buildUserFromDTO(CreateCustomerDTO dto, RoleResponseDTO role, PasswordEncoder encoder) {
        return User.builder()
                .email(dto.getEmail())  // This comes from User class
                .firstname(dto.getFirstName())  // From Person class
                .lastname(dto.getLastName())  // From Person class
                .dob(LocalDate.parse(dto.getDob()))  // From Person class
                .phoneNumber(dto.getPhoneNumber())  // From Person class
                .password(encoder.encode(dto.getPassword()))  // From User class
                .roles(Set.of())  // From User class
                .build();
    }

    public Customer buildCustomer(User user) {
        return Customer.builder()
                .profile(user)
                .build();
    }

    public Account buildAccount(Customer customer) {
        return Account.builder()
                .user(customer.getProfile())
                .accountNumber(generateAccountNumber())
                .build();
    }

    public String generateAccountNumber() {
        String prefix = "21"; // Bank identifier
        long number = 100000000L + new Random().nextLong(900000000L);
        return prefix + Math.abs(number); // Example: 2112345678
    }
}