package com.bookstore.order.config;

import com.bookstore.order.messaging.RabbitNames;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
