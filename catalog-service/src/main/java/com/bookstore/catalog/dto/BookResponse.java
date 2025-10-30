package com.bookstore.catalog.dto;

import java.math.BigDecimal;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        BigDecimal price,
        Integer stock
) {}