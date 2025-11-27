package com.koerber.assignment.inventory.dto;

import com.koerber.assignment.inventory.entity.ProductType;
import lombok.Data;

@Data
public class InventoryUpdateRequest {
    private String productId;
    private ProductType productType;
    private Integer quantity;
}
