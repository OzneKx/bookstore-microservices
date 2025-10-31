package com.bookstore.catalog.controller;

import com.bookstore.catalog.dto.BookRequest;
import com.bookstore.catalog.dto.BookResponse;
import com.bookstore.catalog.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {BookController.class})
public class BookControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    private BookRequest bookRequest;
    private BookResponse bookResponse;

    @BeforeEach
    void setup() {
        bookRequest = new BookRequest(
                "978-85-333-0227-3",
                "Clean Architecture",
                "Robert C. Martin",
                BigDecimal.valueOf(19.90),
                25
        );

        bookResponse = new BookResponse(
                1L,
                "978-85-333-0227-3",
                "Clean Architecture",
                "Robert C. Martin",
                BigDecimal.valueOf(19.90),
                25
        );
    }

    @Test
    void shouldCreateBook() throws Exception {
        when(bookService.create(bookRequest)).thenReturn(bookResponse);

        mockMvc.perform(post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Architecture"));
    }

    @Test
    void shouldReturnAllBooks() throws Exception {
        when(bookService.getAll()).thenReturn(List.of(bookResponse));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].isbn").value("978-85-333-0227-3"))
                .andExpect(jsonPath("$[0].author").value("Robert C. Martin"));
    }

    @Test
    void shouldReturnBookById() throws Exception {
        when(bookService.getById(1L)).thenReturn(bookResponse);

        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Clean Architecture"));
    }

    @Test
    void shouldUpdateBook() throws Exception {
        when(bookService.update(1L, bookRequest)).thenReturn(bookResponse);

        mockMvc.perform(put("/books/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("978-85-333-0227-3"));
    }

    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L)).andExpect(status().isNoContent());
    }
}
