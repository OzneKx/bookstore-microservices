package com.bookstore.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Represents each book included in an order.")
public record OrderItemRequest(
    @Schema(description = "ID of the book being ordered.", example = "11")
    @NotNull(message = "Book ID is required")
    Long bookId,

    @Schema(description = "Quantity of this book in the order.", example = "3")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {
}
