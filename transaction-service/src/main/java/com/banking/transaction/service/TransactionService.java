package com.banking.transaction.service;

import com.banking.transaction.dto.TransactionInput;
import com.banking.transaction.kafka.TransactionEventProducer;
import com.banking.transaction.model.Transaction;
import com.banking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer eventProducer;

    @Transactional
    public Transaction createTransaction(TransactionInput input) {
        log.info("Creating transaction from {} to {}", input.getFromAccountNumber(), input.getToAccountNumber());

        Transaction transaction = Transaction.builder()
                .transactionId(generateTransactionId())
                .fromAccountNumber(input.getFromAccountNumber())
                .toAccountNumber(input.getToAccountNumber())
                .amount(input.getAmount())
                .currency(input.getCurrency())
                .transactionType(input.getTransactionType())
                .status("PENDING")
                .description(input.getDescription())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Send Kafka event with dynamic values
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("transactionId", savedTransaction.getTransactionId());
        eventData.put("fromAccountNumber", savedTransaction.getFromAccountNumber());
        eventData.put("toAccountNumber", savedTransaction.getToAccountNumber());
        eventData.put("amount", savedTransaction.getAmount());
        eventData.put("currency", savedTransaction.getCurrency());
        eventData.put("transactionType", savedTransaction.getTransactionType());
        eventData.put("status", savedTransaction.getStatus());
        eventData.put("description", savedTransaction.getDescription());
        eventData.put("transactionDate", savedTransaction.getTransactionDate().toString());
        eventProducer.sendTransactionCreatedEvent(eventData);

        return savedTransaction;
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public Transaction getTransactionByTransactionId(String transactionId) {
        return transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with transactionId: " + transactionId));
    }

    public List<Transaction> getTransactionsByFromAccount(String fromAccountNumber) {
        return transactionRepository.findByFromAccountNumber(fromAccountNumber);
    }

    public List<Transaction> getTransactionsByToAccount(String toAccountNumber) {
        return transactionRepository.findByToAccountNumber(toAccountNumber);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional
    public Transaction updateTransactionStatus(String transactionId, String status) {
        log.info("Updating transaction status: {} to {}", transactionId, status);

        Transaction transaction = getTransactionByTransactionId(transactionId);
        transaction.setStatus(status);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Send appropriate Kafka event based on status
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("transactionId", updatedTransaction.getTransactionId());
        eventData.put("fromAccountNumber", updatedTransaction.getFromAccountNumber());
        eventData.put("toAccountNumber", updatedTransaction.getToAccountNumber());
        eventData.put("amount", updatedTransaction.getAmount());
        eventData.put("status", updatedTransaction.getStatus());
        eventData.put("updatedAt", LocalDateTime.now().toString());

        if ("COMPLETED".equalsIgnoreCase(status)) {
            eventProducer.sendTransactionCompletedEvent(eventData);
        } else if ("FAILED".equalsIgnoreCase(status)) {
            eventProducer.sendTransactionFailedEvent(eventData);
        }

        return updatedTransaction;
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
