package com.koerber.assignment.order.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String productId;
    private String productType; // Needed for Inventory Factory
    private Integer quantity;
}
