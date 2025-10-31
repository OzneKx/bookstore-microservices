package com.bookstore.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Payload returned after book operations")
public record BookResponse(
        @Schema(description = "Book ID", example = "1")
        Long id,
        @Schema(description = "Book ISBN", example = "978-85-333-0227-3")
        String isbn,
        @Schema(description = "Book title", example = "Clean Architecture")
        String title,
        @Schema(description = "Book author", example = "Robert C. Martin")
        String author,
        @Schema(description = "Book price", example = "19.90")
        BigDecimal price,
        @Schema(description = "Available stock", example = "25")
        Integer stock
) {
}