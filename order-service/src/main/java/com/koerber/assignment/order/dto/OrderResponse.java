package com.koerber.assignment.order.dto;

import com.koerber.assignment.order.entity.OrderStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderResponse {
    private UUID orderId;
    private OrderStatus status;
    private String message;
}
