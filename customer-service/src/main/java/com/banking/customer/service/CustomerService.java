package com.banking.customer.service;

import com.banking.customer.dto.CustomerInput;
import com.banking.customer.dto.UpdateCustomerInput;
import com.banking.customer.kafka.CustomerEventProducer;
import com.banking.customer.model.Customer;
import com.banking.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerEventProducer eventProducer;

    @Transactional
    public Customer createCustomer(CustomerInput input) {
        log.info("Creating customer: {} {}", input.getFirstName(), input.getLastName());

        // Check if email already exists
        if (customerRepository.findByEmail(input.getEmail()).isPresent()) {
            throw new RuntimeException("Customer with email " + input.getEmail() + " already exists");
        }

        Customer customer = Customer.builder()
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .email(input.getEmail())
                .phoneNumber(input.getPhoneNumber())
                .dateOfBirth(input.getDateOfBirth())
                .address(input.getAddress())
                .city(input.getCity())
                .country(input.getCountry())
                .postalCode(input.getPostalCode())
                .status("ACTIVE")
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        // Send Kafka event with dynamic values
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("customerId", savedCustomer.getId());
        eventData.put("firstName", savedCustomer.getFirstName());
        eventData.put("lastName", savedCustomer.getLastName());
        eventData.put("email", savedCustomer.getEmail());
        eventData.put("phoneNumber", savedCustomer.getPhoneNumber());
        eventData.put("dateOfBirth", savedCustomer.getDateOfBirth().toString());
        eventData.put("address", savedCustomer.getAddress());
        eventData.put("city", savedCustomer.getCity());
        eventData.put("country", savedCustomer.getCountry());
        eventData.put("postalCode", savedCustomer.getPostalCode());
        eventData.put("status", savedCustomer.getStatus());
        eventData.put("createdAt", savedCustomer.getCreatedAt().toString());
        eventProducer.sendCustomerCreatedEvent(eventData);

        return savedCustomer;
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));
    }

    public List<Customer> getCustomersByCity(String city) {
        return customerRepository.findByCity(city);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer updateCustomer(UpdateCustomerInput input) {
        log.info("Updating customer with id: {}", input.getId());

        Customer customer = getCustomerById(input.getId());

        if (input.getEmail() != null && !input.getEmail().equals(customer.getEmail())) {
            if (customerRepository.findByEmail(input.getEmail()).isPresent()) {
                throw new RuntimeException("Email " + input.getEmail() + " is already in use");
            }
            customer.setEmail(input.getEmail());
        }

        if (input.getPhoneNumber() != null) {
            customer.setPhoneNumber(input.getPhoneNumber());
        }

        if (input.getAddress() != null) {
            customer.setAddress(input.getAddress());
        }

        if (input.getCity() != null) {
            customer.setCity(input.getCity());
        }

        if (input.getPostalCode() != null) {
            customer.setPostalCode(input.getPostalCode());
        }

        Customer updatedCustomer = customerRepository.save(customer);

        // Send Kafka event with dynamic values
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("customerId", updatedCustomer.getId());
        eventData.put("email", updatedCustomer.getEmail());
        eventData.put("phoneNumber", updatedCustomer.getPhoneNumber());
        eventData.put("address", updatedCustomer.getAddress());
        eventData.put("city", updatedCustomer.getCity());
        eventData.put("postalCode", updatedCustomer.getPostalCode());
        eventData.put("updatedAt", updatedCustomer.getUpdatedAt().toString());
        eventProducer.sendCustomerUpdatedEvent(eventData);

        return updatedCustomer;
    }

    @Transactional
    public Customer updateCustomerStatus(Long id, String status) {
        log.info("Updating customer status: {} to {}", id, status);

        Customer customer = getCustomerById(id);
        customer.setStatus(status);
        Customer updatedCustomer = customerRepository.save(customer);

        // Send Kafka event with dynamic values
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("customerId", updatedCustomer.getId());
        eventData.put("status", updatedCustomer.getStatus());
        eventData.put("updatedAt", updatedCustomer.getUpdatedAt().toString());
        eventProducer.sendCustomerUpdatedEvent(eventData);

        return updatedCustomer;
    }

    @Transactional
    public boolean deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);
        Customer customer = getCustomerById(id);
        
        // Send Kafka event with dynamic values
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("customerId", customer.getId());
        eventData.put("email", customer.getEmail());
        eventData.put("deletedAt", LocalDateTime.now().toString());
        eventProducer.sendCustomerDeletedEvent(eventData);

        customerRepository.delete(customer);
        return true;
    }
}
