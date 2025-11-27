package com.koerber.assignment.inventory.service;

import com.koerber.assignment.inventory.dto.InventoryUpdateRequest;
import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.entity.Product;
import com.koerber.assignment.inventory.repository.BatchRepository;
import com.koerber.assignment.inventory.repository.ProductRepository;
import com.koerber.assignment.inventory.strategy.InventoryStrategy;
import com.koerber.assignment.inventory.strategy.InventoryStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class InventoryService {
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final InventoryStrategyFactory strategyFactory;

    public InventoryService(ProductRepository productRepository, BatchRepository batchRepository, InventoryStrategyFactory strategyFactory) {
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.strategyFactory = strategyFactory;
    }

    @Transactional
    public void updateInventory(InventoryUpdateRequest request) {
        log.info("Processing inventory update for Product: {}", request.getProductId());

        // 1. Fetch Product and Batches
        Product product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        List<Batch> batches = batchRepository.findByProduct(product);

        // 2. Get Strategy from Factory
        InventoryStrategy strategy = strategyFactory.getStrategy(request.getProductType());

        // 3. Sort Batches (Business Logic)
        List<Batch> sortedBatches = strategy.sortBatches(batches);

        // 4. Execute Deduction
        strategy.executeDeduction(sortedBatches, request.getQuantity());

        // 5. Save Changes (JPA manages dirty checking, but explicit save is safe)
        batchRepository.saveAll(sortedBatches);

        log.info("Inventory updated successfully for Product: {}", request.getProductId());
    }

    public List<Batch> getBatches(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Even for reading, we might want to apply the sorting strategy
        InventoryStrategy strategy = strategyFactory.getStrategy(product.getProductType());
        return strategy.sortBatches(batchRepository.findByProduct(product));
    }
}
