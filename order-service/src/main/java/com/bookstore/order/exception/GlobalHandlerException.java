package com.bookstore.order.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalHandlerException {
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(
            Exception ex, HttpServletRequest httpServletRequest) {
        return buildExceptionStructure(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected server error.", httpServletRequest);
    }
}
