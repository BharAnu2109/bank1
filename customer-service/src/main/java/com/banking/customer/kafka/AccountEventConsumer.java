package com.banking.customer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "account-created", groupId = "customer-service-group")
    public void handleAccountCreated(String message) {
        try {
            Map<String, Object> accountData = objectMapper.readValue(message, Map.class);
            log.info("Received account created event: {}", accountData);
            // Process account created event if needed
        } catch (Exception e) {
            log.error("Error processing account created event", e);
        }
    }
}
