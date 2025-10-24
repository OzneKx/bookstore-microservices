package com.bookstore.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard structure for error responses.")
public record ErrorResponse(

        @Schema(
                description = "HTTP status code of the error.",
                example = "500"
        )
        int status,

        @Schema(
                description = "Error message.",
                example = "An unexpected error occurred. Please contact support."
        )
        String message,

        @Schema(
                description = "Technical name of the exception for debugging.",
                example = "NullPointerException"
        )
        String error,

        @Schema(
                description = "Timestamp when the error occurred.",
                example = "2025-10-24T21:00:00"
        )
        LocalDateTime timestamp
    ) {
}