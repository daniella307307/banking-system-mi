package com.daniella.bms.services.services;

import com.daniella.bms.dtos.request.customer.CreateCustomerDTO;
import com.daniella.bms.dtos.request.customer.UpdateCustomerDTO;
import com.daniella.bms.dtos.response.customer.CustomerResponseDTO;
import com.daniella.bms.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICustomerService {
    CustomerResponseDTO createCustomer(CreateCustomerDTO dto);
    Customer getCustomerById(UUID customerId);
    Page<Customer> getAllCustomers(UUID customerId, UpdateCustomerDTO dto);
    void deleteCustomer(UUID customerId);

    Customer updateCustomer(UUID id, UpdateCustomerDTO dto);

    Page<Customer> getAllCustomers(Pageable pageable);
}
