package com.koerber.assignment.inventory.service;


import com.koerber.assignment.inventory.dto.InventoryUpdateRequest;
import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.entity.Product;
import com.koerber.assignment.inventory.exception.ProductNotFoundException;
import com.koerber.assignment.inventory.mapper.InventoryLoadMapper;
import com.koerber.assignment.inventory.repository.BatchRepository;
import com.koerber.assignment.inventory.repository.ProductRepository;
import com.koerber.assignment.inventory.strategy.InventoryStrategy;
import com.koerber.assignment.inventory.strategy.InventoryStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BatchRepository batchRepository;

    @Mock
    private InventoryStrategyFactory strategyFactory;

    @Mock
    private InventoryLoadMapper inventoryLoadMapper;

    @Mock
    private InventoryStrategy inventoryStrategy;

    @InjectMocks
    private InventoryService inventoryService;

    private static final String PRODUCT_ID = "PROD-123";
    private static final String productType = "FOOD";
    private Product mockProduct;
    private InventoryUpdateRequest updateRequest;
    private List<Batch> mockBatches;

    @BeforeEach
    void setUp() {
        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setProductId(PRODUCT_ID);
        mockProduct.setProductType(productType);

        updateRequest = new InventoryUpdateRequest();
        updateRequest.setProductId(PRODUCT_ID);
        updateRequest.setQuantity(10);
        updateRequest.setProductType("FOOD");

        mockBatches = List.of(new Batch(), new Batch());
    }

    @Test
    void updateInventory_Success_ShouldCallSaveAllAndDeduction() {
        when(productRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
        when(batchRepository.findByProduct(mockProduct)).thenReturn(mockBatches);
        when(strategyFactory.getStrategy(updateRequest.getProductType())).thenReturn(inventoryStrategy);
        when(inventoryStrategy.sortBatches(mockBatches)).thenReturn(mockBatches);

        inventoryService.updateInventory(updateRequest);

        verify(inventoryStrategy, times(1)).executeDeduction(mockBatches, updateRequest.getQuantity());
        verify(batchRepository, times(1)).saveAll(mockBatches);
    }

    @Test
    void updateInventory_ProductNotFound_ShouldThrowException() {
        when(productRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                inventoryService.updateInventory(updateRequest));

        verifyNoInteractions(batchRepository, strategyFactory, inventoryLoadMapper);
    }

    @Test
    void updateInventory_DeductionFails_ShouldPropagateException() {
        // Arrange
        when(productRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
        when(batchRepository.findByProduct(mockProduct)).thenReturn(mockBatches);
        when(strategyFactory.getStrategy(updateRequest.getProductType())).thenReturn(inventoryStrategy);
        when(inventoryStrategy.sortBatches(mockBatches)).thenReturn(mockBatches);

        // Simulate insufficient stock failure (or validation error)
        doThrow(new RuntimeException("Insufficient Stock")).when(inventoryStrategy).executeDeduction(anyList(), anyInt());

        assertThrows(RuntimeException.class, () ->
                inventoryService.updateInventory(updateRequest)
        );

        verify(batchRepository, never()).saveAll(anyList());
    }

    @Test
    void getBatches_Success_ShouldReturnSortedBatches() {
        // Arrange
        when(productRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.of(mockProduct));
        when(batchRepository.findByProduct(mockProduct)).thenReturn(mockBatches);
        when(strategyFactory.getStrategy(mockProduct.getProductType())).thenReturn(inventoryStrategy);
        when(inventoryStrategy.sortBatches(mockBatches)).thenReturn(mockBatches);

        // Act
        List<Batch> result = inventoryService.getBatches(PRODUCT_ID);

        // Assert
        assertEquals(mockBatches, result);
        verify(inventoryStrategy, times(1)).sortBatches(mockBatches);
    }

    @Test
    void getBatches_ProductNotFound_ShouldThrowException() {
        when(productRepository.findByProductId(PRODUCT_ID)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () ->
                inventoryService.getBatches(PRODUCT_ID));

        verifyNoInteractions(batchRepository, strategyFactory, inventoryLoadMapper);
    }


}