package com.bookstore.catalog.service;

import com.bookstore.catalog.data.entity.Book;
import com.bookstore.catalog.data.mapper.BookMapper;
import com.bookstore.catalog.data.repository.BookRepository;
import com.bookstore.catalog.dto.BookRequest;
import com.bookstore.catalog.dto.BookResponse;
import com.bookstore.catalog.exception.IsbnAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock private BookMapper bookMapper;
    @Mock private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private BookRequest bookRequest;
    private Book book;
    private BookResponse bookResponse;

    @BeforeEach
    void setUp() {
        bookRequest = new BookRequest(
                "978-85-333-0227-3", "Clean Code",
                "Robert C. Martin", BigDecimal.valueOf(99.90), 10
        );

        book = new Book(1L, bookRequest.isbn(), bookRequest.title(),
                bookRequest.author(), bookRequest.price(), bookRequest.stock());

        bookResponse = new BookResponse(
                1L, bookRequest.isbn(), bookRequest.title(),
                bookRequest.author(), bookRequest.price(), bookRequest.stock()
        );
    }

    @Test
    void shouldCreateBookSuccessfully() {
        when(bookRepository.findByIsbnIgnoreCase(bookRequest.isbn())).thenReturn(Optional.empty());
        when(bookMapper.toEntity(bookRequest)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

        BookResponse result = bookService.create(bookRequest);

        assertThat(result.id()).isEqualTo(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldThrowExceptionWhenIsbnAlreadyExists() {
        when(bookRepository.findByIsbnIgnoreCase(bookRequest.isbn())).thenReturn(Optional.of(book));

        assertThatThrownBy(() -> bookService.create(bookRequest)).isInstanceOf(IsbnAlreadyExistsException.class);
    }

    @Test
    void shouldReturnAllBooksSuccessfully() {
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

        List<BookResponse> result = bookService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().title()).isEqualTo("Clean Code");
    }

    @Test
    void shouldUpdateBookSuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.findByIsbnIgnoreCase(bookRequest.isbn())).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

        BookResponse updated = bookService.update(1L, bookRequest);

        assertThat(updated.isbn()).isEqualTo(bookRequest.isbn());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldDeleteBookSuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository).delete(book);
    }
}
