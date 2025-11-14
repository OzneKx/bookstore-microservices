package com.bookstore.order.service;

import com.bookstore.order.client.CatalogClient;
import com.bookstore.order.data.entity.Order;
import com.bookstore.order.data.entity.OrderItem;
import com.bookstore.order.data.entity.OrderStatus;
import com.bookstore.order.data.mapper.OrderItemMapper;
import com.bookstore.order.data.mapper.OrderMapper;
import com.bookstore.order.data.repository.OrderItemRepository;
import com.bookstore.order.data.repository.OrderRepository;
import com.bookstore.order.dto.OrderRequest;
import com.bookstore.order.dto.OrderResponse;
import com.bookstore.order.messaging.event.OrderCreatedEvent;
import com.bookstore.order.messaging.publisher.OrderEventPublisher;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderEventPublisher orderEventPublisher;

    private final RestTemplate restTemplate;
    private final CatalogClient catalogClient;


    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                        OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                        OrderEventPublisher orderEventPublisher, RestTemplate restTemplate, CatalogClient catalogClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.orderEventPublisher = orderEventPublisher;
        this.restTemplate = restTemplate;
        this.catalogClient = catalogClient;
    }

    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order order = initializeOrder(request.userId());
        List<OrderItem> items = processOrderItems(request, order);
        orderRepository.save(order);
        publishOrderCreatedEvent(order);
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

    private Order initializeOrder(Long userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotal(BigDecimal.ZERO);
        return orderRepository.save(order);
    }

    private List<OrderItem> processOrderItems(OrderRequest request, Order order) {
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

        return items;
    }

    private void publishOrderCreatedEvent(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotal()
        );
        orderEventPublisher.publishOrderCreated(event);
    }

    private String getAuthHeader() {
        var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing request context");
        }

        String token = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }

        return token;
    }

    private BigDecimal getBookPrice(Long bookId) {
        String token = getAuthHeader();
        return catalogClient.getBookById(bookId, token).price();
    }
}
