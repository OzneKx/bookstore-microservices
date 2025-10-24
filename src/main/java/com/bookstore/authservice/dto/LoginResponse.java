package com.bookstore.authservice.dto;

public record LoginResponse(String token, String type,
                            Long expiresIn, UserResponse user) {
}
