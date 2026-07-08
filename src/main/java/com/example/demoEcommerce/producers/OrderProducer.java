package com.example.demoEcommerce.producers;

import com.example.demoEcommerce.events.OrderCreatedEvent;
import com.example.demoEcommerce.kafkaConstants.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publishOrderCreatedEvent(OrderCreatedEvent event) {

        log.info("Publishing OrderCreatedEvent : {}", event);

        kafkaTemplate.send(
                KafkaTopics.ORDER_CREATED,
                event.getOrderId().toString(),
                event
        );
    }
}