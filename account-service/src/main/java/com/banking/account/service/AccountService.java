package com.banking.account.service;

import com.banking.account.dto.AccountInput;
import com.banking.account.dto.UpdateBalanceInput;
import com.banking.account.kafka.AccountEventProducer;
import com.banking.account.model.Account;
import com.banking.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountEventProducer eventProducer;

    @Transactional
    public Account createAccount(AccountInput input) {
        log.info("Creating account for customer ID: {}", input.getCustomerId());

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .customerId(input.getCustomerId())
                .accountType(input.getAccountType())
                .balance(input.getInitialBalance())
                .currency(input.getCurrency())
                .status("ACTIVE")
                .build();

        Account savedAccount = accountRepository.save(account);

        // Send Kafka event with dynamic values
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("accountId", savedAccount.getId());
        eventData.put("accountNumber", savedAccount.getAccountNumber());
        eventData.put("customerId", savedAccount.getCustomerId());
        eventData.put("accountType", savedAccount.getAccountType());
        eventData.put("balance", savedAccount.getBalance());
        eventData.put("currency", savedAccount.getCurrency());
        eventData.put("status", savedAccount.getStatus());
        eventData.put("createdAt", savedAccount.getCreatedAt().toString());
        eventProducer.sendAccountCreatedEvent(eventData);

        return savedAccount;
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));
    }

    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found with number: " + accountNumber));
    }

    public List<Account> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public Account updateBalance(UpdateBalanceInput input) {
        log.info("Updating balance for account: {}", input.getAccountNumber());

        Account account = getAccountByNumber(input.getAccountNumber());
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance;

        if ("CREDIT".equalsIgnoreCase(input.getOperationType())) {
            newBalance = currentBalance.add(input.getAmount());
        } else if ("DEBIT".equalsIgnoreCase(input.getOperationType())) {
            if (currentBalance.compareTo(input.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance");
            }
            newBalance = currentBalance.subtract(input.getAmount());
        } else {
            throw new RuntimeException("Invalid operation type: " + input.getOperationType());
        }

        account.setBalance(newBalance);
        Account updatedAccount = accountRepository.save(account);

        // Send Kafka event with dynamic values
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("accountId", updatedAccount.getId());
        eventData.put("accountNumber", updatedAccount.getAccountNumber());
        eventData.put("previousBalance", currentBalance);
        eventData.put("newBalance", newBalance);
        eventData.put("amount", input.getAmount());
        eventData.put("operationType", input.getOperationType());
        eventData.put("updatedAt", LocalDateTime.now().toString());
        eventProducer.sendBalanceUpdatedEvent(eventData);

        return updatedAccount;
    }

    @Transactional
    public Account updateAccountStatus(String accountNumber, String status) {
        log.info("Updating account status: {} to {}", accountNumber, status);

        Account account = getAccountByNumber(accountNumber);
        account.setStatus(status);
        Account updatedAccount = accountRepository.save(account);

        // Send Kafka event with dynamic values
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("accountId", updatedAccount.getId());
        eventData.put("accountNumber", updatedAccount.getAccountNumber());
        eventData.put("status", updatedAccount.getStatus());
        eventData.put("updatedAt", updatedAccount.getUpdatedAt().toString());
        eventProducer.sendAccountUpdatedEvent(eventData);

        return updatedAccount;
    }

    @Transactional
    public boolean deleteAccount(Long id) {
        log.info("Deleting account with id: {}", id);
        Account account = getAccountById(id);
        accountRepository.delete(account);
        return true;
    }

    private String generateAccountNumber() {
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
