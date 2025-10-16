package com.banking.account.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendAccountCreatedEvent(Map<String, Object> accountData) {
        try {
            String message = objectMapper.writeValueAsString(accountData);
            kafkaTemplate.send("account-created", message);
            log.info("Account created event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending account created event", e);
        }
    }

    public void sendAccountUpdatedEvent(Map<String, Object> accountData) {
        try {
            String message = objectMapper.writeValueAsString(accountData);
            kafkaTemplate.send("account-updated", message);
            log.info("Account updated event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending account updated event", e);
        }
    }

    public void sendBalanceUpdatedEvent(Map<String, Object> balanceData) {
        try {
            String message = objectMapper.writeValueAsString(balanceData);
            kafkaTemplate.send("balance-updated", message);
            log.info("Balance updated event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending balance updated event", e);
        }
    }
}
