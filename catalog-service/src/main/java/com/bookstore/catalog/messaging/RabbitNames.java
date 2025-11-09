package com.bookstore.catalog.messaging;

public final class RabbitNames {
    private RabbitNames() {}

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String ORDER_QUEUE = "order.queue";
}