package com.koerber.assignment.inventory.Controller;


import com.koerber.assignment.inventory.dto.InventoryUpdateTempRequest;
import com.koerber.assignment.inventory.dto.InventoryUpdateRequest;
import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<Batch>> getInventory(@PathVariable String productId) {
        return ResponseEntity.ok(inventoryService.getBatches(productId));
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateInventory(@RequestBody InventoryUpdateRequest request) {
        try {
            log.info("InventoryUpdateRequest received for id: {} and type: {}", request.getProductId(), request.getProductType());
            inventoryService.updateInventory(request);
            return ResponseEntity.ok("status: Success || Inventory updated");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("status: Failed due to " + e.getMessage());
        }
    }

    @PutMapping("/loadInventory")
    public ResponseEntity<String> loadInventory(@RequestBody List<InventoryUpdateTempRequest> requests) {
        try {
            inventoryService.loadInventory(requests);
            return ResponseEntity.ok("Successfully loaded initial inventory data.");
        } catch (Exception e) {
            log.error("Failed to load initial inventory data.", e);
            return ResponseEntity.badRequest().body("Failed to load inventory: " + e.getMessage());
        }
    }
}