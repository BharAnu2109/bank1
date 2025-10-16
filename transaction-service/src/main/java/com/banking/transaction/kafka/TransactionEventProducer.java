package com.banking.transaction.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendTransactionCreatedEvent(Map<String, Object> transactionData) {
        try {
            String message = objectMapper.writeValueAsString(transactionData);
            kafkaTemplate.send("transaction-created", message);
            log.info("Transaction created event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending transaction created event", e);
        }
    }

    public void sendTransactionCompletedEvent(Map<String, Object> transactionData) {
        try {
            String message = objectMapper.writeValueAsString(transactionData);
            kafkaTemplate.send("transaction-completed", message);
            log.info("Transaction completed event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending transaction completed event", e);
        }
    }

    public void sendTransactionFailedEvent(Map<String, Object> transactionData) {
        try {
            String message = objectMapper.writeValueAsString(transactionData);
            kafkaTemplate.send("transaction-failed", message);
            log.info("Transaction failed event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending transaction failed event", e);
        }
    }
}
