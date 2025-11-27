package com.koerber.assignment.inventory.Controller;


import com.koerber.assignment.inventory.dto.InventoryUpdateRequest;
import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inventory")
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
    public ResponseEntity<?> updateInventory(@RequestBody InventoryUpdateRequest request) {
        try {
            inventoryService.updateInventory(request);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "message", "Inventory updated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "FAILED", "message", e.getMessage()));
        }
    }
}
