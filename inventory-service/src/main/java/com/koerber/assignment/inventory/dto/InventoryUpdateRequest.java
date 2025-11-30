package com.koerber.assignment.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryUpdateRequest {
    private String productId;
    private String productType;
    private Integer quantity;
}
