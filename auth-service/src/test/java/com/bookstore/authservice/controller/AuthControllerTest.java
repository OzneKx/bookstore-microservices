package com.bookstore.authservice.controller;

import com.bookstore.authservice.dto.LoginRequest;
import com.bookstore.authservice.dto.LoginResponse;
import com.bookstore.authservice.dto.UserRequest;
import com.bookstore.authservice.dto.UserResponse;
import com.bookstore.authservice.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {AuthController.class})
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserRequest userRequest = new UserRequest("Kenzo", "kenzoalbuqk@gmail.com", "123456");
        UserResponse userResponse = new UserResponse(1L, "Kenzo", "kenzoalbuqk@gmail.com", "ROLE_USER");

        Mockito.when(authService.register(userRequest)).thenReturn(userResponse);

        mockMvc.perform(post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("kenzoalbuqk@gmail.com"));
    }

    @Test
    void shouldLoginUserSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest("kenzoalbuqk@gmail.com", "123456");
        LoginResponse loginResponse = new LoginResponse("jwtToken123", "Bearer", 3600L,
                new UserResponse(1L, "Kenzo", "kenzoalbuqk@gmail.com", "ROLE_USER"));

        Mockito.when(authService.login(loginRequest)).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwtToken123"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.user.email").value("kenzoalbuqk@gmail.com"));
    }

    @Test
    void shouldLogoutUserSuccessfully() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer jwt123"))
                .andExpect(status().isNoContent());
    }
}
