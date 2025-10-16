package com.banking.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerInput {
    @NotNull(message = "Customer ID is required")
    private Long id;

    private String phoneNumber;

    @Email(message = "Email should be valid")
    private String email;

    private String address;

    private String city;

    private String postalCode;
}
