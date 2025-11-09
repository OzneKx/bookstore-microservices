package com.bookstore.order.messaging.publisher;

import com.bookstore.order.messaging.event.OrderCreatedEvent;
import com.bookstore.order.messaging.RabbitNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publishOrderCreated(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitNames.ORDER_EXCHANGE, RabbitNames.ORDER_CREATED_QUEUE, event);
        log.info("OrderCreatedEvent published: {}", event);
    }
}
