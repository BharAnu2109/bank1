package com.banking.transaction.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceUpdateConsumer {
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "balance-updated", groupId = "transaction-service-group")
    public void handleBalanceUpdate(String message) {
        try {
            Map<String, Object> balanceData = objectMapper.readValue(message, Map.class);
            log.info("Received balance update event: {}", balanceData);
            // Process balance update event if needed
        } catch (Exception e) {
            log.error("Error processing balance update event", e);
        }
    }
}
