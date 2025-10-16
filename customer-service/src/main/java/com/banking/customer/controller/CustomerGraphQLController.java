package com.banking.customer.controller;

import com.banking.customer.dto.CustomerInput;
import com.banking.customer.dto.UpdateCustomerInput;
import com.banking.customer.model.Customer;
import com.banking.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CustomerGraphQLController {
    private final CustomerService customerService;

    @QueryMapping
    public Customer customerById(@Argument Long id) {
        log.info("GraphQL Query: customerById - id: {}", id);
        return customerService.getCustomerById(id);
    }

    @QueryMapping
    public Customer customerByEmail(@Argument String email) {
        log.info("GraphQL Query: customerByEmail - email: {}", email);
        return customerService.getCustomerByEmail(email);
    }

    @QueryMapping
    public List<Customer> customersByCity(@Argument String city) {
        log.info("GraphQL Query: customersByCity - city: {}", city);
        return customerService.getCustomersByCity(city);
    }

    @QueryMapping
    public List<Customer> allCustomers() {
        log.info("GraphQL Query: allCustomers");
        return customerService.getAllCustomers();
    }

    @MutationMapping
    public Customer createCustomer(@Argument @Valid CustomerInput input) {
        log.info("GraphQL Mutation: createCustomer - input: {}", input);
        return customerService.createCustomer(input);
    }

    @MutationMapping
    public Customer updateCustomer(@Argument @Valid UpdateCustomerInput input) {
        log.info("GraphQL Mutation: updateCustomer - input: {}", input);
        return customerService.updateCustomer(input);
    }

    @MutationMapping
    public Customer updateCustomerStatus(@Argument Long id, @Argument String status) {
        log.info("GraphQL Mutation: updateCustomerStatus - id: {}, status: {}", id, status);
        return customerService.updateCustomerStatus(id, status);
    }

    @MutationMapping
    public Boolean deleteCustomer(@Argument Long id) {
        log.info("GraphQL Mutation: deleteCustomer - id: {}", id);
        return customerService.deleteCustomer(id);
    }
}
