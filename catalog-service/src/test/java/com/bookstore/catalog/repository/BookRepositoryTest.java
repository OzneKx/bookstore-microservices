package com.bookstore.catalog.repository;

import com.bookstore.catalog.data.entity.Book;
import com.bookstore.catalog.data.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldFindBookByIsbnIgnoreCase() {
        Book book = new Book(null, "TEST-123", "Repo Book", "Repo Author", BigDecimal.TEN, 5);
        bookRepository.save(book);

        Optional<Book> bookFound = bookRepository.findByIsbnIgnoreCase("TEST-123");

        assertThat(bookFound).isPresent();
        assertThat(bookFound.get().getTitle()).isEqualTo("Repo Book");
    }
}
