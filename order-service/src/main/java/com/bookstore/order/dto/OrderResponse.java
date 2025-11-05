package com.bookstore.order.dto;

import com.bookstore.order.data.entity.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response model representing an order and its items.")
public record OrderResponse(
        @Schema(description = "Order ID", example = "11")
        Long id,
        @Schema(description = "User ID who placed the order", example = "42")
        Long userId,
        @Schema(description = "Total value of the order", example = "125.50")
        BigDecimal total,
        @Schema(description = "Current order status", example = "PENDING")
        OrderStatus orderStatus,
        @Schema(description = "Creation timestamp", example = "2025-11-01T10:15:30")
        LocalDateTime createdAt,
        @Schema(description = "List of items included in the order")
        List<OrderItemResponse> items
) {}
