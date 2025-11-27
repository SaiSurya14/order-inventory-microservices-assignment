package com.koerber.assignment.order.client;

import com.koerber.assignment.order.dto.InventoryUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class InventoryClient {

    private final RestTemplate restTemplate;

    public InventoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public boolean updateInventory(String productId, String productType, Integer quantity) {
        String url = inventoryServiceUrl + "/update";
        InventoryUpdateRequest request = new InventoryUpdateRequest(productId, productType, quantity);

        try {
            log.info("Calling Inventory Service: {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            log.error("Inventory update failed: {}", e.getResponseBodyAsString());
            return false; // 400 Bad Request (Insufficient Stock)
        } catch (Exception e) {
            log.error("Inventory Service is down or error occurred", e);
            throw new RuntimeException("Inventory Service Unavailable");
        }
    }
}
