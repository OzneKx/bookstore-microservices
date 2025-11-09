package com.bookstore.order.service;

import com.bookstore.order.data.entity.Order;
import com.bookstore.order.data.entity.OrderItem;
import com.bookstore.order.data.entity.OrderStatus;
import com.bookstore.order.data.mapper.OrderItemMapper;
import com.bookstore.order.data.mapper.OrderMapper;
import com.bookstore.order.data.repository.OrderItemRepository;
import com.bookstore.order.data.repository.OrderRepository;
import com.bookstore.order.dto.OrderRequest;
import com.bookstore.order.dto.OrderResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    private final RestTemplate restTemplate;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        OrderMapper orderMapper, OrderItemMapper orderItemMapper, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.restTemplate = restTemplate;
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotal(BigDecimal.ZERO);
        orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var itemRequest : request.items()) {
            BigDecimal price = getBookPrice(itemRequest.bookId());
            OrderItem item = orderItemMapper.toEntity(itemRequest);
            item.setOrderId(order.getId());
            item.setPrice(price);
            total = total.add(price.multiply(BigDecimal.valueOf(itemRequest.quantity())));
            items.add(item);
        }

        orderItemRepository.saveAll(items);
        order.setTotal(total);
        orderRepository.save(order);

        return orderMapper.toResponse(order, orderItemMapper.toResponseList(items));
    }

    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream()
                .map(order -> {
                    var items = orderItemRepository.findByOrderId(order.getId());
                    return orderMapper.toResponse(order, orderItemMapper.toResponseList(items));
                })
                .toList();
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        var items = orderItemRepository.findByOrderId(order.getId());
        return orderMapper.toResponse(order, orderItemMapper.toResponseList(items));
    }

    @Transactional
    public void cancel(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private BigDecimal getBookPrice(Long id) {
        String url = "http://catalog-service/books/" + id;
        try {
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("price")) {
                return new BigDecimal(response.get("price").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch book price from catalog-service: " + e.getMessage());
        }
        throw new RuntimeException("Book not found in catalog-service");
    }
}
