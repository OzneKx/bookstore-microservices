package com.bookstore.authservice.service;

import com.bookstore.authservice.data.entity.Token;
import com.bookstore.authservice.data.entity.User;
import com.bookstore.authservice.data.mapper.UserMapper;
import com.bookstore.authservice.data.repository.TokenRepository;
import com.bookstore.authservice.data.repository.UserRepository;
import com.bookstore.authservice.dto.LoginRequest;
import com.bookstore.authservice.dto.LoginResponse;
import com.bookstore.authservice.dto.UserRequest;
import com.bookstore.authservice.dto.UserResponse;
import com.bookstore.authservice.exception.EmailAlreadyExistsException;
import com.bookstore.authservice.exception.InvalidEmailException;
import com.bookstore.authservice.exception.InvalidTokenException;
import com.bookstore.authservice.security.CustomUserDetailsService;
import com.bookstore.authservice.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock private UserRepository userRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserMapper userMapper;
    @Mock private JwtUtil jwtUtil;
    @Mock private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRequest userRequest = new UserRequest("Kenzo", "kenzoalbuqk@gmail.com", "Kenzo123$");

        User user = new User();
        user.setName(userRequest.name());
        user.setEmail(userRequest.email());
        user.setPassword(userRequest.password());

        when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.password())).thenReturn("encodedPass");
        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(
                new UserResponse(1L, "Kenzo", "kenzoalbuqk@gmail.com", "ROLE_USER")
        );

        UserResponse resp = authService.register(userRequest);

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(userRequest.password());
        assertEquals("Kenzo", resp.name());
        assertEquals("kenzoalbuqk@gmail.com", resp.email());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        UserRequest userRequest = new UserRequest("Kenzo", "kenzoalbuqk@gmail.com", "Kenzo123$");

        when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(userRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenEmailInvalidDuringRegistration() {
        UserRequest userRequest = new UserRequest("Kenzo", "invalid-email", "Kenzo123$");

        assertThrows(InvalidEmailException.class, () -> authService.register(userRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldEncodePasswordBeforeSaving() {
        UserRequest userRequest = new UserRequest("Kenzo", "kenzo@bookstore.com", "Kenzo123$");

        when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);
        when(passwordEncoder.encode(userRequest.password())).thenReturn("hashedPassword");
        when(userMapper.toEntity(userRequest)).thenReturn(new User());

        authService.register(userRequest);

        verify(passwordEncoder).encode(userRequest.password());
        verify(userRepository).save(argThat(u -> u.getPassword().equals("hashedPassword")));
    }

    @Test
    void shouldAuthenticateUserSuccessfully() {
        String email = "kenzoalbuqk@gmail.com";
        String password = "Kenzo123$";

        User user = new User();
        user.setEmail(email);
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("hashedPassword")
                .authorities(singletonList(() -> "ROLE_USER"))
                .build();

        when(customUserDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwtToken123");
        when(jwtUtil.getExpirationInSeconds()).thenReturn(3600L);
        when(userMapper.toResponse(user))
                .thenReturn(new UserResponse(1L, "Kenzo", email, "ROLE_USER"));
        when(tokenRepository.findAllValidTokensByUserId(user.getId())).thenReturn(List.of());

        LoginResponse loginResponse = authService.login(new LoginRequest(email, password));
        assertNotNull(loginResponse);
        assertEquals("jwtToken123", loginResponse.token());
        assertEquals("Bearer", loginResponse.type());
        assertEquals(3600L, loginResponse.expiresIn());
        assertNotNull(loginResponse.user());
        assertEquals(email, loginResponse.user().email());

        verify(tokenRepository).save(any());
        verify(passwordEncoder).matches(password, user.getPassword());
        verify(jwtUtil).generateToken(userDetails);
    }

    @Test
    void shouldThrowExceptionWhenEmailInvalidDuringLogin() {
        LoginRequest loginRequest = new LoginRequest("invalid-email", "Kenzo123$");

        assertThrows(InvalidEmailException.class, () -> authService.login(loginRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPasswordInvalidDuringLogin() {
        String email = "kenzoalbuqk@gmail.com";
        String password = "invalid";

        User user = new User();
        user.setEmail(email);
        user.setPassword("hashedPassword");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(new LoginRequest(email, password)));
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void shouldLogoutSuccessfully() {
        Token token = new Token();
        token.setToken("jwtToken123");
        token.setRevoked(false);
        token.setExpired(false);

        when(tokenRepository.findByToken("jwtToken123")).thenReturn(Optional.of(token));

        authService.logout("Bearer jwtToken123");

        assertTrue(token.isRevoked());
        assertTrue(token.isExpired());
        verify(tokenRepository).save(token);
    }

    @Test
    void shouldThrowExceptionWhenTokenNotFound() {
        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.empty());
        assertThrows(InvalidTokenException.class, () -> authService.logout("Bearer missing"));
    }
}
