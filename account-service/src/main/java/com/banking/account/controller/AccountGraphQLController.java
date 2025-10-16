package com.banking.account.controller;

import com.banking.account.dto.AccountInput;
import com.banking.account.dto.UpdateBalanceInput;
import com.banking.account.model.Account;
import com.banking.account.service.AccountService;
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
public class AccountGraphQLController {
    private final AccountService accountService;

    @QueryMapping
    public Account accountById(@Argument Long id) {
        log.info("GraphQL Query: accountById - id: {}", id);
        return accountService.getAccountById(id);
    }

    @QueryMapping
    public Account accountByNumber(@Argument String accountNumber) {
        log.info("GraphQL Query: accountByNumber - accountNumber: {}", accountNumber);
        return accountService.getAccountByNumber(accountNumber);
    }

    @QueryMapping
    public List<Account> accountsByCustomerId(@Argument Long customerId) {
        log.info("GraphQL Query: accountsByCustomerId - customerId: {}", customerId);
        return accountService.getAccountsByCustomerId(customerId);
    }

    @QueryMapping
    public List<Account> allAccounts() {
        log.info("GraphQL Query: allAccounts");
        return accountService.getAllAccounts();
    }

    @MutationMapping
    public Account createAccount(@Argument @Valid AccountInput input) {
        log.info("GraphQL Mutation: createAccount - input: {}", input);
        return accountService.createAccount(input);
    }

    @MutationMapping
    public Account updateBalance(@Argument @Valid UpdateBalanceInput input) {
        log.info("GraphQL Mutation: updateBalance - input: {}", input);
        return accountService.updateBalance(input);
    }

    @MutationMapping
    public Account updateAccountStatus(@Argument String accountNumber, @Argument String status) {
        log.info("GraphQL Mutation: updateAccountStatus - accountNumber: {}, status: {}", accountNumber, status);
        return accountService.updateAccountStatus(accountNumber, status);
    }

    @MutationMapping
    public Boolean deleteAccount(@Argument Long id) {
        log.info("GraphQL Mutation: deleteAccount - id: {}", id);
        return accountService.deleteAccount(id);
    }
}
