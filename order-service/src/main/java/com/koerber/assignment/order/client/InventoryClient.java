package com.koerber.assignment.order.client;

import com.koerber.assignment.order.dto.InventoryUpdateRequest;
import com.koerber.assignment.order.dto.OrderRequest;
import com.koerber.assignment.order.exceptions.InventoryUnavailableException;
import com.koerber.assignment.order.mapper.InventoryMapper;
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
    private final InventoryMapper inventoryMapper;

    public InventoryClient(RestTemplate restTemplate, InventoryMapper inventoryMapper) {
        this.restTemplate = restTemplate;
        this.inventoryMapper = inventoryMapper;
    }

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public boolean updateInventory(OrderRequest orderRequest) {

        InventoryUpdateRequest request = inventoryMapper.toInventoryUpdateRequest(orderRequest);

        try {
            log.info("Calling Inventory Service: {}", inventoryServiceUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(inventoryServiceUrl, request, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException e) {
            log.error("Inventory update failed: {}", e.getResponseBodyAsString());
            return false; // 400 Bad Request
        } catch (Exception e) {
            log.error("Inventory Service is down or error occurred", e);
            throw new InventoryUnavailableException("Inventory Service Unavailable or error occurred");
        }
    }
}
