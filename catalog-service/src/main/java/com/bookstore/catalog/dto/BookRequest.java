package com.bookstore.catalog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Schema(description = "Payload used to create or update a book")
public record BookRequest(
        @Schema(description = "Book ISBN", example = "978-85-333-0227-3")
        @NotBlank(message = "ISBN is required")
        String isbn,

        @Schema(description = "Book title", example = "Clean Architecture")
        @NotBlank(message = "Title is required")
        String title,

        @Schema(description = "Book author", example = "Robert C. Martin")
        @NotBlank(message = "Author is required")
        String author,

        @Schema(description = "Book price", example = "19.90")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "Price must have up to 2 decimal places")
        BigDecimal price,

        @Schema(description = "Stock quantity", example = "25")
        @Min(value = 0, message = "Stock must not be negative")
        Integer stock
) {
}
