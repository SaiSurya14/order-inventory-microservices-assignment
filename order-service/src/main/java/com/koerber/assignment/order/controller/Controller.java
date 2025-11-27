package com.koerber.assignment.order.controller;

import com.koerber.assignment.order.dto.OrderRequest;
import com.koerber.assignment.order.dto.OrderResponse;
import com.koerber.assignment.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class Controller {
    private final OrderService orderService;

    public Controller(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/placeOrder")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest request) {
        OrderResponse response = orderService.placeOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
