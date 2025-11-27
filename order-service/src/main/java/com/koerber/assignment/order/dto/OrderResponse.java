package com.koerber.assignment.order.dto;

import com.koerber.assignment.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long orderId;
    private OrderStatus status;
    private String message;
}
