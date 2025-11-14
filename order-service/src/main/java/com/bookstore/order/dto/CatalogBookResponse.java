package com.bookstore.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record CatalogBookResponse(
        @Schema(description = "Order ID", example = "11")
        Long id,
        @Schema(description = "Book title", example = "Clean Architecture")
        String title,
        @Schema(description = "Book price", example = "19.90")
        BigDecimal price
) {
}
