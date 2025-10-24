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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenRepository tokenRepository,
                       UserMapper userMapper, JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    public UserResponse register(UserRequest userRequest) {
        validateEmailFormat(userRequest.email());
        validateEmailUniqueness(userRequest.email());

        User user = userMapper.toEntity(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.password()));
        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = findByEmail(loginRequest.email());
        validatePassword(loginRequest.password(), user);

        String jwtToken = generateJwtTokenForUser(user);
        saveToken(jwtToken, user);

        Long expiresIn = jwtUtil.getExpirationInSeconds();
        UserResponse userResponse = userMapper.toResponse(user);

        return new LoginResponse(jwtToken, "Bearer", expiresIn, userResponse);
    }

    public void logout(String rawToken) {
        Token token = findExtractedToken(extractToken(rawToken));
        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);
    }

    private void validateEmailFormat(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException(email);
        }
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new InvalidEmailException(email));
    }

    private void validatePassword(String rawPassword, User user) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
    }

    private String generateJwtTokenForUser(User user) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        return jwtUtil.generateToken(userDetails);
    }

    private void saveToken(String jwtToken, User user) {
        revokeExistingTokens(user);
        createAndSaveNewToken(jwtToken, user);
    }

    private void revokeExistingTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUserId(user.getId());
        if (validTokens.isEmpty()) return;

        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private void createAndSaveNewToken(String jwtToken, User user) {
        Token token = new Token();
        token.setToken(jwtToken);
        token.setUser(user);
        token.setExpired(false);
        token.setRevoked(false);
        tokenRepository.save(token);
    }

    private String extractToken(String rawToken) {
        return rawToken.replace("Bearer", "").trim();
    }

    private Token findExtractedToken(String extractedToken) {
        return tokenRepository.findByToken(extractedToken).orElseThrow(InvalidTokenException::new);
    }
}
