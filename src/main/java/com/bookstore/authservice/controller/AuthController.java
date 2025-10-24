package com.bookstore.authservice.controller;

import com.bookstore.authservice.dto.AuthMessageResponse;
import com.bookstore.authservice.dto.LoginRequest;
import com.bookstore.authservice.dto.LoginResponse;
import com.bookstore.authservice.dto.UserResponse;
import com.bookstore.authservice.dto.UserRequest;
import com.bookstore.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for user register, login and logout using JWT token.")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Register a new user.",
            description = "Creates a new user in the system along with their main account.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Email already exists"),
                    @ApiResponse(responseCode = "409", description = "Email already exists",
                            content = @Content(schema = @Schema(implementation = AuthMessageResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest userRequest) {
        UserResponse userResponse = authService.register(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Operation(
            summary = "Authenticate user and obtain JWT token.",
            description = "Validates user credentials and returns a JWT token to be used for authorized API requests.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
                    @ApiResponse(responseCode = "401", description = "Bad credentials"),
                    @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(
            summary = "Logout user.",
            description = "Revokes the JWT token associated with the current session.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Logout successful. No content returned"),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired token"),
                    @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content)
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
