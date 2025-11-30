package com.koerber.assignment.inventory.service;

import com.koerber.assignment.inventory.dto.InventoryUpdateTempRequest;
import com.koerber.assignment.inventory.dto.InventoryUpdateRequest;
import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.entity.Product;
import com.koerber.assignment.inventory.exception.ProductNotFoundException;
import com.koerber.assignment.inventory.mapper.InventoryLoadMapper;
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
    private final InventoryLoadMapper inventoryLoadMapper;

    public InventoryService(ProductRepository productRepository, BatchRepository batchRepository, InventoryStrategyFactory strategyFactory, InventoryLoadMapper inventoryLoadMapper) {
        this.productRepository = productRepository;
        this.batchRepository = batchRepository;
        this.strategyFactory = strategyFactory;
        this.inventoryLoadMapper = inventoryLoadMapper;
    }

    @Transactional
    public void updateInventory(InventoryUpdateRequest request) {
        log.info("Processing inventory update for Product: {}", request.getProductId());

        Product product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        List<Batch> batches = batchRepository.findByProduct(product);
        InventoryStrategy strategy = strategyFactory.getStrategy(request.getProductType());
        List<Batch> sortedBatches = strategy.sortBatches(batches);
        strategy.executeDeduction(sortedBatches, request.getQuantity());
        batchRepository.saveAll(sortedBatches);

        log.info("Inventory updated successfully for Product: {}", request.getProductId());
    }

    public List<Batch> getBatches(String productId) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        InventoryStrategy strategy = strategyFactory.getStrategy(product.getProductType());
        return strategy.sortBatches(batchRepository.findByProduct(product));
    }

    @Transactional
    public void loadInventory(List<InventoryUpdateTempRequest> requests) {

        for (InventoryUpdateTempRequest request : requests) {
            if (productRepository.existsByProductId(request.getProductId())) {
                log.warn("Product {} already exists. Skipping this load. ", request.getProductId());
                continue;
            }

            Product product = inventoryLoadMapper.toProduct(request);
            log.info("ENTITY productId after mapping = {}", product.getProductId());
            Product savedProduct = productRepository.save(product);

            List<Batch> batchList = inventoryLoadMapper.toBatchList(request.getBatches());
            batchList.forEach(batch -> batch.setProduct(savedProduct));
            batchRepository.saveAll(batchList);

            log.info("Loaded {} batches for new product: {}", batchList.size(), request.getProductId());
        }
    }
}
