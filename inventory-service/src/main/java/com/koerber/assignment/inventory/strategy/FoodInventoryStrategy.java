package com.koerber.assignment.inventory.strategy;

import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.entity.ProductType;
import com.koerber.assignment.inventory.exception.InsufficientStockException;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FoodInventoryStrategy implements InventoryStrategy {

    @Override
    public ProductType getSupportedType() {
        return ProductType.FOOD;
    }

    @Override
    public List<Batch> sortBatches(List<Batch> batches) {
        return batches.stream()
                .sorted(Comparator.comparing(Batch::getExpiryDate))
                .collect(Collectors.toList());
    }

    @Override
    public void executeDeduction(List<Batch> batches, int quantityNeeded) {
        int remaining = quantityNeeded;

        for (Batch batch : batches) {
            if (remaining <= 0) break;

            int available = batch.getQuantity();
            if (available > 0) {
                if (available >= remaining) {
                    batch.setQuantity(available - remaining);
                    remaining = 0;
                } else {
                    batch.setQuantity(0);
                    remaining -= available;
                }
            }
        }

        if (remaining > 0) {
            throw new InsufficientStockException("Insufficient Stock! Short by: " + remaining);
        }
    }
}
