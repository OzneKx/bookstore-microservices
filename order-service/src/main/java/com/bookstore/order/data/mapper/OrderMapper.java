package com.bookstore.order.data.mapper;

import com.bookstore.order.data.entity.Order;
import com.bookstore.order.dto.OrderItemResponse;
import com.bookstore.order.dto.OrderRequest;
import com.bookstore.order.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "orderStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Order toEntity(OrderRequest orderRequest);

    @Mapping(target = "items", ignore = true)
    default OrderResponse toResponse(Order order, List<OrderItemResponse> items) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotal(),
                order.getOrderStatus(),
                order.getCreatedAt(),
                items
        );
    }
}
