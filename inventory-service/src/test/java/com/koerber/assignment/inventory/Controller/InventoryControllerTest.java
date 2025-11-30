package com.koerber.assignment.inventory.Controller;

import com.koerber.assignment.inventory.dto.InventoryUpdateRequest;
import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @Test
    void getInventory_shouldReturnBatches() {
        String productId = "PROD-001";
        List<Batch> mockBatches = List.of(
                new Batch(1L, 100, LocalDate.now(), null),
                new Batch(2L, 50, LocalDate.now().plusDays(10), null)
        );

        when(inventoryService.getBatches(productId)).thenReturn(mockBatches);

        ResponseEntity<List<Batch>> response = inventoryController.getInventory(productId);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(mockBatches, response.getBody());
        verify(inventoryService, times(1)).getBatches(productId);
    }

    @Test
    void updateInventory_shouldReturnSuccess() {
        InventoryUpdateRequest req = new InventoryUpdateRequest("PROD001", "FOOD", 10);

        ResponseEntity<String> response = inventoryController.updateInventory(req);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        verify(inventoryService).updateInventory(req);
    }

    @Test
    void updateInventory_shouldReturnFailureOnException() {
        InventoryUpdateRequest req = new InventoryUpdateRequest("PROD001", "FOOD", 10);

        doThrow(new RuntimeException("DB error"))
                .when(inventoryService).updateInventory(req);

        ResponseEntity<String> response = inventoryController.updateInventory(req);

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
}