package com.koerber.assignment.inventory.strategy;

import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.entity.ProductType;
import com.koerber.assignment.inventory.exception.InsufficientStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FoodInventoryStrategyTest {

    @InjectMocks
    private FoodInventoryStrategy foodInventoryStrategy;

    @Test
    void getSupportedType_shouldReturnFood() {
        FoodInventoryStrategy strategy = new FoodInventoryStrategy();

        ProductType result = strategy.getSupportedType();

        assertEquals(ProductType.FOOD, result);
    }

    @Test
    void sortBatches_shouldSortByExpiryDateAscending() {
        FoodInventoryStrategy strategy = new FoodInventoryStrategy();

        Batch batch1 = new Batch();
        batch1.setId(1L);
        batch1.setQuantity(10);
        batch1.setExpiryDate(LocalDate.of(2025, 12, 10));

        Batch batch2 = new Batch();
        batch2.setId(2L);
        batch2.setQuantity(20);
        batch2.setExpiryDate(LocalDate.of(2025, 11, 10));

        Batch batch3 = new Batch();
        batch3.setId(3L);
        batch3.setQuantity(30);
        batch3.setExpiryDate(LocalDate.of(2025, 10, 10));

        List<Batch> batches = List.of(batch1, batch2, batch3);

        List<Batch> sorted = foodInventoryStrategy.sortBatches(batches);

        assertEquals(3, sorted.size());
        assertEquals(batch3, sorted.get(0));
        assertEquals(batch2, sorted.get(1));
        assertEquals(batch1, sorted.get(2));
    }

    @Test
    void executeDeduction_shouldDeductFromSingleBatch_whenEnoughStockInFirstBatch() {
        Batch batch1 = new Batch();
        batch1.setQuantity(50);

        Batch batch2 = new Batch();
        batch2.setQuantity(30);

        List<Batch> batches = List.of(batch1, batch2);

        foodInventoryStrategy.executeDeduction(batches, 40);

        assertEquals(10, batch1.getQuantity());
        assertEquals(30, batch2.getQuantity());
    }

    @Test
    void executeDeduction_shouldDeductAcrossMultipleBatches_whenFirstBatchNotEnough() {
        Batch batch1 = new Batch();
        batch1.setQuantity(20);

        Batch batch2 = new Batch();
        batch2.setQuantity(30);

        List<Batch> batches = List.of(batch1, batch2);

        foodInventoryStrategy.executeDeduction(batches, 40);

        assertEquals(0, batch1.getQuantity());
        assertEquals(10, batch2.getQuantity()); // 30 - (40 - 20)
    }

    @Test
    void executeDeduction_shouldDrainAllBatches_whenExactlyEnoughStock() {
        Batch batch1 = new Batch();
        batch1.setQuantity(20);

        Batch batch2 = new Batch();
        batch2.setQuantity(30);

        List<Batch> batches = List.of(batch1, batch2);

        foodInventoryStrategy.executeDeduction(batches, 50);

        assertEquals(0, batch1.getQuantity());
        assertEquals(0, batch2.getQuantity());
    }

    @Test
    void executeDeduction_shouldThrowException_whenInsufficientStock() {
        Batch batch1 = new Batch();
        batch1.setQuantity(10);

        Batch batch2 = new Batch();
        batch2.setQuantity(5);

        List<Batch> batches = List.of(batch1, batch2);

        InsufficientStockException ex = assertThrows(
                InsufficientStockException.class,
                () -> foodInventoryStrategy.executeDeduction(batches, 20)
        );

        assertTrue(ex.getMessage().contains("Short by: 5"));
    }
}