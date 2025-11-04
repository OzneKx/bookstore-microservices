package com.bookstore.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long bookId,
        Integer quantity,
        BigDecimal price
) {
}
