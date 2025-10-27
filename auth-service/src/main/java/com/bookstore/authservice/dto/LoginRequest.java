package com.bookstore.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Used for authenticating a user using email and password credentials.")
public record LoginRequest(
        @Schema(
                description = "Email address associated with the registered user account.",
                example = "kenzoalbuqk@gmail.com"
        )
        String email,

        @Schema(
                description = "User's password.",
                example = "Bookstore123$"
        )
        String password
    ) {
}
