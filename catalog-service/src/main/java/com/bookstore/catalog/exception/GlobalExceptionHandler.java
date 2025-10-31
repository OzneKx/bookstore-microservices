package com.bookstore.catalog.exception;

import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<ApiError> buildExceptionStructure(
            HttpStatus httpStatus, String message, HttpServletRequest httpServletRequest) {
        var error = new ApiError(
                LocalDateTime.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                message,
                httpServletRequest.getRequestURI()
        );
        return ResponseEntity.status(httpStatus).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest httpServletRequest) {
        return buildExceptionStructure(HttpStatus.BAD_REQUEST, ex.getMessage(), httpServletRequest);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(
            EntityNotFoundException ex, HttpServletRequest httpServletRequest) {
        return buildExceptionStructure(HttpStatus.NOT_FOUND, ex.getMessage(), httpServletRequest);
    }

    @ExceptionHandler(IsbnAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleIsbnAlreadyExistsException(
            IsbnAlreadyExistsException ex, HttpServletRequest httpServletRequest) {
        return buildExceptionStructure(HttpStatus.CONFLICT, ex.getMessage(), httpServletRequest);
    }

    public ResponseEntity<ApiError> handleJwtException(
            JwtException ex, HttpServletRequest httpServletRequest) {
        return buildExceptionStructure(HttpStatus.UNAUTHORIZED,
                "Invalid or expired token.", httpServletRequest);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest httpServletRequest) {
        return buildExceptionStructure(HttpStatus.FORBIDDEN, "Access denied.", httpServletRequest);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, HttpServletRequest httpServletRequest) {
        return buildExceptionStructure(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error.", httpServletRequest);
    }
}
