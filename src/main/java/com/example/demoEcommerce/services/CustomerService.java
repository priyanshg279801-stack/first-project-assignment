package com.example.demoEcommerce.services;




import com.example.demoEcommerce.dto.CustomerRequest;
import com.example.demoEcommerce.dto.CustomerResponse;
import com.example.demoEcommerce.entities.Customer;
import com.example.demoEcommerce.repositories.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(CustomerRequest request) {

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());

        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }

        if (customerRepository.existsByPhoneNumber(customer.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists.");
        }

        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        Customer saved = customerRepository.save(customer);

        return new CustomerResponse(
                saved.getFirstName() + " " + saved.getLastName(),
                saved.getEmail()
        );
    }

    public CustomerResponse getCustomerResponseById(Long id) {
        Customer customer = getCustomerById(id);

        return new CustomerResponse(
                customer.getFirstName() + " " + customer.getLastName(),
                customer.getEmail()
        );
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Customer not found with id : " + id));
    }

    public Customer updateCustomer(Long id, com.example.demoEcommerce.entities.@Valid Customer updatedCustomer) {

        Customer customer = getCustomerById(id);

        customer.setFirstName(updatedCustomer.getFirstName());
        customer.setLastName(updatedCustomer.getLastName());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        customer.setAddress(updatedCustomer.getAddress());
        customer.setUpdatedAt(LocalDateTime.now());

        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {

        Customer customer = getCustomerById(id);

        customerRepository.delete(customer);
    }
}