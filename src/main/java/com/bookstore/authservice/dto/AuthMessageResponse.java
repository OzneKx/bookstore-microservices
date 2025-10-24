package com.bookstore.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Message response used for status or confirmation messages.")
public record AuthMessageResponse(
        @Schema(
                description = "Message describing the result of an operation.",
                example = "User registered successfully"
        )
        String message
    ) {
}
