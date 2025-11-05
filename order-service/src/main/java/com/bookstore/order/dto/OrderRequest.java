package com.bookstore.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "Request model for creating a new order.")
public record OrderRequest(
        @Schema(description = "ID of the user placing the order.", example = "1")
        @NotNull(message = "User ID is required")
        Long userId,

        @Schema(description = "List of items included in the order.")
        @NotEmpty(message = "Order items cannot be empty")
        List<OrderItemRequest> items
) {
}
