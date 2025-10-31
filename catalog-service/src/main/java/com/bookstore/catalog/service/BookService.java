package com.bookstore.catalog.service;

import com.bookstore.catalog.data.entity.Book;
import com.bookstore.catalog.data.mapper.BookMapper;
import com.bookstore.catalog.data.repository.BookRepository;
import com.bookstore.catalog.dto.BookRequest;
import com.bookstore.catalog.dto.BookResponse;
import com.bookstore.catalog.exception.IsbnAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    public BookService(BookRepository bookRepository, BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Transactional
    public BookResponse create(BookRequest bookRequest) {
        bookRepository.findByIsbnIgnoreCase(bookRequest.isbn()).ifPresent(book -> {
            throw new IsbnAlreadyExistsException(bookRequest.isbn());
        });

        Book book = bookMapper.toEntity(bookRequest);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    public List<BookResponse> getAll() {
        return bookRepository.findAll().stream().map(bookMapper::toResponse).toList();
    }

    public BookResponse getById(Long id) {
        Book book = checkExistentBook(id);
        return bookMapper.toResponse(book);
    }

    @Transactional
    public BookResponse update(Long id, BookRequest bookRequest) {
        Book book = checkExistentBook(id);

        Optional<Book> existingBook = bookRepository.findByIsbnIgnoreCase(bookRequest.isbn());
        if (existingBook.isPresent() && !existingBook.get().getId().equals(id)) {
            throw new IsbnAlreadyExistsException(bookRequest.isbn());
        }

        bookMapper.updateEntityFromRequest(bookRequest, book);
        Book updatedBook = bookRepository.save(book);
        return bookMapper.toResponse(updatedBook);
    }

    @Transactional
    public void delete(Long id) {
        Book book = checkExistentBook(id);
        bookRepository.delete(book);
    }

    private Book checkExistentBook(Long id) {
        return bookRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
