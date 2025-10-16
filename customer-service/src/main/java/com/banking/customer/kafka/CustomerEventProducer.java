package com.banking.customer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCustomerCreatedEvent(Map<String, Object> customerData) {
        try {
            String message = objectMapper.writeValueAsString(customerData);
            kafkaTemplate.send("customer-created", message);
            log.info("Customer created event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending customer created event", e);
        }
    }

    public void sendCustomerUpdatedEvent(Map<String, Object> customerData) {
        try {
            String message = objectMapper.writeValueAsString(customerData);
            kafkaTemplate.send("customer-updated", message);
            log.info("Customer updated event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending customer updated event", e);
        }
    }

    public void sendCustomerDeletedEvent(Map<String, Object> customerData) {
        try {
            String message = objectMapper.writeValueAsString(customerData);
            kafkaTemplate.send("customer-deleted", message);
            log.info("Customer deleted event sent: {}", message);
        } catch (Exception e) {
            log.error("Error sending customer deleted event", e);
        }
    }
}
