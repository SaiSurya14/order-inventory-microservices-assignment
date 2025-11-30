package com.koerber.assignment.order.service;

import com.koerber.assignment.order.client.InventoryClient;
import com.koerber.assignment.order.dto.OrderRequest;
import com.koerber.assignment.order.dto.OrderResponse;
import com.koerber.assignment.order.entity.OrderStatus;
import com.koerber.assignment.order.entity.Orders;
import com.koerber.assignment.order.mapper.OrderMapper;
import com.koerber.assignment.order.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.orderMapper = orderMapper;
    }

    public OrderResponse placeOrder(OrderRequest request) {
        log.info("Placing order for Product: {}", request.getProductId());

        Orders order = orderMapper.toEntity(request);

        // Call Inventory Service
        boolean inventoryUpdated = inventoryClient.updateInventory(request);

        if (!inventoryUpdated) {
            order.setStatus(OrderStatus.FAILED);
            order.setFailureReason("Insufficient Stock or Invalid Product");
            order.setOrderId(UUID.randomUUID());
            orderRepository.save(order);
            return orderMapper.toResponse(order, "Insufficient Stock or Invalid Product");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderId(UUID.randomUUID());
        orderRepository.save(order);
        log.info("Order placed successfully with id: {}", order.getOrderId());

        return orderMapper.toResponse(order, "Order placed successfully");

    }
}
