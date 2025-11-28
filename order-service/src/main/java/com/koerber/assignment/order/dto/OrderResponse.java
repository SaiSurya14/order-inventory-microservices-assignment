package com.koerber.assignment.order.dto;

import com.koerber.assignment.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID orderId;
    private OrderStatus status;
    private String message;
}
