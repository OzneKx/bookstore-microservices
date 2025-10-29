package com.bookstore.authservice.integration;

import com.bookstore.authservice.dto.LoginRequest;
import com.bookstore.authservice.dto.UserRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthIntegrationTest {
    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldRegisterAndLoginSuccessfully() {
        String baseUrl = "http://localhost:" + port + "/auth";
        String email = "kenzo" + System.currentTimeMillis() + "@bookstore.com";

        UserRequest userRequest = new UserRequest("Kenzo", email, "123456");
        ResponseEntity<String> registerResponse =
                testRestTemplate.postForEntity(baseUrl + "/register", userRequest, String.class);
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        LoginRequest loginRequest = new LoginRequest("kenzoalbuqk@gmail.com", "123456");
        ResponseEntity<String> loginResponse =
                testRestTemplate.postForEntity(baseUrl + "/login", loginRequest, String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        Assertions.assertNotNull(loginResponse.getBody());
        assertTrue(loginResponse.getBody().contains("token"));
    }
}
