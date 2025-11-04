package com.bookstore.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotEmpty(message = "Order items cannot be empty")
        List<OrderItemRequest> items
) {
}
