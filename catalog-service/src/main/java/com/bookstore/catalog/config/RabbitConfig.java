package com.bookstore.catalog.config;

import com.bookstore.catalog.messaging.RabbitNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(RabbitNames.ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(RabbitNames.ORDER_QUEUE, true);
    }

    @Bean
    public Binding binding(Queue orderQueue, TopicExchange orderExchange) {
        return BindingBuilder
                .bind(orderQueue)
                .to(orderExchange)
                .with(RabbitNames.ORDER_CREATED_ROUTING_KEY);
    }
}
