package com.bookstore.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Response model for individual order items.")
public record OrderItemResponse(
        @Schema(description = "Order Item ID", example = "11")
        Long id,
        @Schema(description = "Book ID", example = "11")
        Long bookId,
        @Schema(description = "Quantity ordered", example = "3")
        Integer quantity,
        @Schema(description = "Unit price of the book at the time of purchase", example = "59.90")
        BigDecimal price
) {
}