package com.bookstore.catalog.controller;

import com.bookstore.catalog.dto.BookRequest;
import com.bookstore.catalog.dto.BookResponse;
import com.bookstore.catalog.exception.ApiError;
import com.bookstore.catalog.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Endpoints for managing books in the catalog (CRUD with JWT security).")
public class BookController {

    private final BookService bookService;
    public BookController(BookService bookService) { this.bookService = bookService; }

    @Operation(
            summary = "Create a new book",
            description = "Adds a new book to the catalog.",
            responses = {
                @ApiResponse(responseCode = "201", description = "Book created",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
                @ApiResponse(responseCode = "400", description = "Validation error or duplicated ISBN",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized (missing or invalid JWT)",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(responseCode = "500", description = "Unexpected server error",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest bookRequest) {
        BookResponse bookResponse = bookService.create(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookResponse);
    }

    @Operation(
            summary = "Get all books",
            description = "Retrieves all books registered in the catalog.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Books retrieved",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @Operation(
            summary = "Get a book by ID",
            description = "Retrieves a specific book by its ID.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Book retrieved",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@Parameter(description = "Book ID") @PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @Operation(
            summary = "Update a book",
            description = "Updates an existing book by its ID (validates duplicated ISBN against other records).",
            responses = {
                @ApiResponse(responseCode = "200", description = "Book updated",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
                @ApiResponse(responseCode = "400", description = "Validation error or duplicated ISBN",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@Parameter(description = "Book ID") @PathVariable Long id,
                                               @Valid @RequestBody BookRequest bookRequest) {
        return ResponseEntity.ok(bookService.update(id, bookRequest));
    }

    @Operation(
            summary = "Delete a book",
            description = "Removes a book from the catalog by its ID.",
            responses = {
                @ApiResponse(responseCode = "204", description = "Book deleted"),
                @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
                @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "Book ID") @PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
