package com.bookstore.catalog.messaging.listener;

import com.bookstore.catalog.messaging.RabbitNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    @RabbitListener(queues = RabbitNames.ORDER_QUEUE)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("OrderCreatedEvent received in Catalog Service: {}", event);
    }
}
