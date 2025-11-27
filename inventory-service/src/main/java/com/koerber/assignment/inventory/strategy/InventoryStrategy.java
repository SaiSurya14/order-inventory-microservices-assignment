package com.koerber.assignment.inventory.strategy;


import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.entity.ProductType;

import java.util.List;

public interface InventoryStrategy {

    ProductType getSupportedType();

    List<Batch> sortBatches(List<Batch> batches);

    void executeDeduction(List<Batch> batches, int quantityNeeded);
}
