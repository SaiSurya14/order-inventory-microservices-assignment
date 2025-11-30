package com.koerber.assignment.inventory.dto;

import com.koerber.assignment.inventory.entity.ProductType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class InventoryLoadRequest {

    private String productId;
    private String productName;
    private ProductType productType;
    private List<BatchData> batches;

    @Data
    public static class BatchData {
        private Integer quantity;
        private LocalDate expiryDate;
    }
}
