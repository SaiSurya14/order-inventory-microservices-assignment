package com.koerber.assignment.inventory.dto;

import java.time.LocalDate;

public class InventoryResponse {
    private Long batchId;
    private Long productId;
    private Integer quantity;
    private LocalDate expiryDate;
}
