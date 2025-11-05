package com.bookstore.order.controller;

import com.bookstore.order.dto.OrderRequest;
import com.bookstore.order.dto.OrderResponse;
import com.bookstore.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Endpoints for managing book orders.")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
        summary = "Create a new order",
        description = "Registers a new book order for a given user, fetching book prices from the catalog-service.",
        responses = {
                @ApiResponse(responseCode = "201", description = "Order created successfully",
                        content = @Content(schema = @Schema(implementation = OrderResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "500", description = "Unexpected server error")
        }
    )
    @PostMapping()
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.create(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @Operation(
        summary = "Get all orders",
        description = "Retrieves all orders.",
        responses = {
                @ApiResponse(responseCode = "200", description = "List of all orders",
                        content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class)))),
                @ApiResponse(responseCode = "500", description = "Unexpected server error")
        }
    )
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @Operation(
        summary = "Get order by ID",
        description = "Retrieves a specific order by ID.",
        responses = {
                @ApiResponse(responseCode = "200", description = "Order found",
                        content = @Content(schema = @Schema(implementation = OrderResponse.class))),
                @ApiResponse(responseCode = "404", description = "Order not found")
        }
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @Operation(
            summary = "Cancel an order",
            description = "Cancels an order by updating its status to CANCELLED.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
