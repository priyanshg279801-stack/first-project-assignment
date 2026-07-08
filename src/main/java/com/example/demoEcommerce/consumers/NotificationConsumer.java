package com.example.demoEcommerce.consumers;

import com.example.demoEcommerce.events.OrderCreatedEvent;
import com.example.demoEcommerce.kafkaConstants.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationConsumer {

    @KafkaListener(
            topics = KafkaTopics.ORDER_CREATED,
            groupId = "notification-group"
    )
    public void consume(OrderCreatedEvent event) {

        log.info("Notification Service received : {}", event);

        // TODO:
        // Send Email
        // Send SMS
    }
}
