package com.bookstore.authservice.dto;

public record UserRequest(String name, String email, String password) {
}
