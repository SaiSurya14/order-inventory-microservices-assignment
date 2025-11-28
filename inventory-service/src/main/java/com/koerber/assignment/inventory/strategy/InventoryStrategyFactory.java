package com.koerber.assignment.inventory.strategy;

import com.koerber.assignment.inventory.entity.ProductType;
import com.koerber.assignment.inventory.exception.InvalidStrategyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class InventoryStrategyFactory{

    private final List<InventoryStrategy> strategies;

    public InventoryStrategyFactory(List<InventoryStrategy> strategies) {
        this.strategies = strategies;
    }

    public InventoryStrategy getStrategy(ProductType type) {
        log.info("Resolving strategy for Product Type: {}", type);

        return strategies.stream()
                .filter(strategy -> strategy.getSupportedType() == type)
                .findFirst()
                .orElseThrow(() -> new InvalidStrategyException("No strategy found for type: " + type));
    }
}
