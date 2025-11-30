package com.koerber.assignment.order.client;

import com.koerber.assignment.order.dto.InventoryUpdateRequest;
import com.koerber.assignment.order.dto.OrderRequest;
import com.koerber.assignment.order.mapper.InventoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private InventoryClient inventoryClient;

    @Test
    void updateInventory_whenSuccess_returnsTrue() {
        ReflectionTestUtils.setField(inventoryClient, "inventoryServiceUrl",
                "http://inventory-service/update");

        OrderRequest orderRequest = new OrderRequest();
        InventoryUpdateRequest invReq = new InventoryUpdateRequest();

        when(inventoryMapper.toInventoryUpdateRequest(orderRequest)).thenReturn(invReq);
        when(restTemplate.postForEntity("http://inventory-service/update", invReq, String.class))
                .thenReturn(ResponseEntity.ok("OK"));

        boolean result = inventoryClient.updateInventory(orderRequest);

        assertTrue(result);
        verify(restTemplate).postForEntity("http://inventory-service/update", invReq, String.class);
        verifyNoMoreInteractions(restTemplate);
    }

}