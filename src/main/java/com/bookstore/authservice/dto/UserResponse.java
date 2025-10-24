package com.bookstore.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Represents a registered user with identification and role information.")
public record UserResponse(

        @Schema(description = "Unique identifier of the user.", example = "1")
        Long id,

        @Schema(description = "Full name of the user.", example = "Kenzo Albuquerque")
        String name,

        @Schema(description = "Email address of the user.", example = "kenzoalbukq@gmail.com")
        String email,

        @Schema(description = "User role within the system.", example = "ROLE_USER")
        String role
    ) {
}
