package com.banking.transaction.controller;

import com.banking.transaction.dto.TransactionInput;
import com.banking.transaction.model.Transaction;
import com.banking.transaction.service.TransactionService;
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
public class TransactionGraphQLController {
    private final TransactionService transactionService;

    @QueryMapping
    public Transaction transactionById(@Argument Long id) {
        log.info("GraphQL Query: transactionById - id: {}", id);
        return transactionService.getTransactionById(id);
    }

    @QueryMapping
    public Transaction transactionByTransactionId(@Argument String transactionId) {
        log.info("GraphQL Query: transactionByTransactionId - transactionId: {}", transactionId);
        return transactionService.getTransactionByTransactionId(transactionId);
    }

    @QueryMapping
    public List<Transaction> transactionsByFromAccount(@Argument String fromAccountNumber) {
        log.info("GraphQL Query: transactionsByFromAccount - fromAccountNumber: {}", fromAccountNumber);
        return transactionService.getTransactionsByFromAccount(fromAccountNumber);
    }

    @QueryMapping
    public List<Transaction> transactionsByToAccount(@Argument String toAccountNumber) {
        log.info("GraphQL Query: transactionsByToAccount - toAccountNumber: {}", toAccountNumber);
        return transactionService.getTransactionsByToAccount(toAccountNumber);
    }

    @QueryMapping
    public List<Transaction> allTransactions() {
        log.info("GraphQL Query: allTransactions");
        return transactionService.getAllTransactions();
    }

    @MutationMapping
    public Transaction createTransaction(@Argument @Valid TransactionInput input) {
        log.info("GraphQL Mutation: createTransaction - input: {}", input);
        return transactionService.createTransaction(input);
    }

    @MutationMapping
    public Transaction updateTransactionStatus(@Argument String transactionId, @Argument String status) {
        log.info("GraphQL Mutation: updateTransactionStatus - transactionId: {}, status: {}", transactionId, status);
        return transactionService.updateTransactionStatus(transactionId, status);
    }
}
