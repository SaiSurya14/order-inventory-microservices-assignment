package com.koerber.assignment.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InventoryUpdateRequest {
    private String productId;
    private String productType;
    private Integer quantity;
}
