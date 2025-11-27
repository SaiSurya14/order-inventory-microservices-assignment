package com.koerber.assignment.order.service;

import com.koerber.assignment.order.client.InventoryClient;
import com.koerber.assignment.order.dto.OrderRequest;
import com.koerber.assignment.order.dto.OrderResponse;
import com.koerber.assignment.order.entity.Orders;
import com.koerber.assignment.order.entity.OrderStatus;
import com.koerber.assignment.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    public OrderResponse placeOrder(OrderRequest request) {
        log.info("Placing order for Product: {}", request.getProductId());

        // 1. Create Order Entity (Initial State)
        Orders order = new Orders();
        order.setProductId(request.getProductId());
        order.setProductType(request.getProductType());
        order.setQuantity(request.getQuantity());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // 2. Call Inventory Service
        boolean success = inventoryClient.updateInventory(
                request.getProductId(),
                request.getProductType(),
                request.getQuantity()
        );

        // 3. Update Order Status based on response
        if (success) {
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            return OrderResponse.builder()
                    .orderId(order.getId())
                    .status(OrderStatus.CONFIRMED)
                    .message("Order placed successfully")
                    .build();
        } else {
            order.setStatus(OrderStatus.FAILED);
            order.setFailureReason("Insufficient Stock or Invalid Product");
            orderRepository.save(order);


            throw new RuntimeException("Order Failed: Insufficient Stock");
        }
    }
}
