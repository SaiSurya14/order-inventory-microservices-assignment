package com.koerber.assignment.order.dto;

import com.koerber.assignment.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private UUID orderId;
    private OrderStatus status;
    private String message;
}
