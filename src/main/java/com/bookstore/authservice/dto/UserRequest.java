package com.bookstore.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Used for registering a new user in the bookstore system.")
public record UserRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 100)
        @JsonProperty("name")
        @Schema(
                description = "Name of the user to be registered.",
                example = "Kenzo de Albuquerque"
        )
        String name,

        @Size(max = 120)
        @JsonProperty("email")
        @Schema(
                description = "Email address used for user authentication.",
                example = "kenzoalbuqk@gmail.com"
        )
        String email,

        @NotBlank(message = "Password is required")
        @JsonProperty("password")
        @Schema(
                description = "User's password.",
                example = "Bookstore123$"
        )
        String password
    ) {
}
