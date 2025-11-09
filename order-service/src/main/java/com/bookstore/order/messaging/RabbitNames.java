package com.bookstore.order.messaging;

public final class RabbitNames {
    private RabbitNames() {}

    public static final String ORDER_EXCHANGE = "orders.exchange";
    public static final String ORDER_CREATED_QUEUE = "order.created";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created.queue";
}
