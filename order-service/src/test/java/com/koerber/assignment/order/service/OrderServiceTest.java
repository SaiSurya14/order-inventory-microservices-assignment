package com.koerber.assignment.order.service;


import com.koerber.assignment.order.client.InventoryClient;
import com.koerber.assignment.order.dto.OrderRequest;
import com.koerber.assignment.order.dto.OrderResponse;
import com.koerber.assignment.order.entity.OrderStatus;
import com.koerber.assignment.order.entity.Orders;
import com.koerber.assignment.order.mapper.OrderMapper;
import com.koerber.assignment.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder_shouldReturnSuccessResponse_whenInventoryUpdatedSuccessfully() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setProductId("APL-001");
        request.setProductType("FOOD");
        request.setProductName("Shimla Apples");
        request.setQuantity(5);

        Orders orderEntity = new Orders();
        orderEntity.setProductId("APL-001");
        orderEntity.setQuantity(5);

        when(orderMapper.toEntity(request)).thenReturn(orderEntity);
        when(inventoryClient.updateInventory(request)).thenReturn(true);

        OrderResponse expectedResponse =
                new OrderResponse(UUID.randomUUID(), OrderStatus.CONFIRMED, "Order placed successfully");

        when(orderMapper.toResponse(any(Orders.class), eq("Order placed successfully")))
                .thenReturn(expectedResponse);

        when(orderRepository.save(any(Orders.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponse response = orderService.placeOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(OrderStatus.CONFIRMED, response.getStatus());
        assertEquals("Order placed successfully", response.getMessage());

        verify(orderMapper, times(1)).toEntity(request);
        verify(inventoryClient, times(1)).updateInventory(request);
    }

    @Test
    void placeOrder_shouldReturnFailureResponse_whenInventoryUpdateFails() {
        // Arrange
        OrderRequest request = new OrderRequest();
        request.setProductId("APL-001");
        request.setProductType("FOOD");
        request.setQuantity(5);

        Orders orderEntity = new Orders();
        when(orderMapper.toEntity(request)).thenReturn(orderEntity);
        when(inventoryClient.updateInventory(request)).thenReturn(false);

        String failureMsg = "Insufficient Stock or Invalid Product";

        OrderResponse expectedResponse =
                new OrderResponse(UUID.randomUUID(), OrderStatus.FAILED, failureMsg);

        when(orderMapper.toResponse(any(Orders.class), eq(failureMsg))).thenReturn(expectedResponse);
        when(orderRepository.save(any(Orders.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        OrderResponse response = orderService.placeOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(OrderStatus.FAILED, response.getStatus());
        assertEquals(failureMsg, response.getMessage());

        verify(orderMapper).toEntity(request);
        verify(inventoryClient).updateInventory(request);
    }
}