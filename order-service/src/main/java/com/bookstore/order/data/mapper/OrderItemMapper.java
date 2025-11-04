package com.bookstore.order.data.mapper;

import com.bookstore.order.data.entity.OrderItem;
import com.bookstore.order.dto.OrderItemRequest;
import com.bookstore.order.dto.OrderItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    OrderItem toEntity(OrderItemRequest orderItemRequest);

    OrderItemResponse toResponse(OrderItem orderItem);

    List<OrderItemResponse> toResponseList(List<OrderItem> entities);
}
