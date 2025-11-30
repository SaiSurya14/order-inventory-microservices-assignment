package com.koerber.assignment.order.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String productId;
    private String productType;
    private String productName;
    private Integer quantity;
}
