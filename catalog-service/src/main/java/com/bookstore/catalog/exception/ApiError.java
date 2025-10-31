package com.bookstore.catalog.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard error payload returned by the API")
public record ApiError(
        @Schema(description = "Timestamp when the error occurred", example = "2025-10-31T21:05:12.345678")
        LocalDateTime timestamp,
        @Schema(description = "HTTP status code", example = "400")
        int status,
        @Schema(description = "HTTP status name", example = "Bad Request")
        String error,
        @Schema(description = "Human-readable error message", example = "ISBN already exists: 978-85-333-0227-3")
        String message,
        @Schema(description = "Request path that caused the error", example = "/books")
        String path
) {
}
