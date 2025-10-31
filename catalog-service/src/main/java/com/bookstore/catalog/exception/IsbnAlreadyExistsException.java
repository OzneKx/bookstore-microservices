package com.bookstore.catalog.exception;

public class IsbnAlreadyExistsException extends RuntimeException {
    public IsbnAlreadyExistsException(String isbn) {
        super("Book with " + isbn + " already exists");
    }
}
