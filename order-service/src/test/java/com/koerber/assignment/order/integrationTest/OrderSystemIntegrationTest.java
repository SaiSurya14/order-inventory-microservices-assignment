package com.koerber.assignment.order.integrationTest;

import com.koerber.assignment.inventory.InventoryServiceApplication;
import com.koerber.assignment.inventory.dto.InventoryUpdateTempRequest;
import com.koerber.assignment.order.OrderServiceApplication;
import com.koerber.assignment.order.TestDataFactory;
import com.koerber.assignment.order.dto.OrderRequest;
import com.koerber.assignment.order.entity.OrderStatus;
import com.koerber.assignment.order.entity.Orders;
import com.koerber.assignment.order.repository.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(
        classes = OrderServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class OrderSystemIntegrationTest {

    private static ConfigurableApplicationContext inventoryContext;
    private static final int INVENTORY_PORT = 8081;
    private static final String INVENTORY_BASE_URL = "http://localhost:" + INVENTORY_PORT + "/inventory";

    @BeforeAll
    static void startInventoryService() {
        inventoryContext = SpringApplication.run(InventoryServiceApplication.class,
                "--server.port=" + INVENTORY_PORT,
                "--spring.datasource.url=jdbc:h2:mem:inventory-sys-db;DB_CLOSE_DELAY=-1",
                "--spring.jpa.hibernate.ddl-auto=create-drop"
        );
    }

    @AfterAll
    static void stopInventoryService() {
        if (inventoryContext != null) {
            inventoryContext.close();
        }
    }

    @LocalServerPort
    private int orderPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    private final TestRestTemplate inventoryClient = new TestRestTemplate();

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        seedInventoryData();
    }

    private void seedInventoryData() {
        String loadUrl = INVENTORY_BASE_URL + "/loadInventory";
        List<InventoryUpdateTempRequest> payload = TestDataFactory.createInventoryLoad();
        inventoryClient.put(loadUrl, payload);
    }

    @Test
    void endToEnd_OrderFlow_ShouldDeductInventoryAndPersistOrder() {
        String orderUrl = "http://localhost:" + orderPort + "/order/placeOrder";
        OrderRequest orderRequest = TestDataFactory.createTestOrderRequest();

        ResponseEntity<String> response = restTemplate.postForEntity(
                orderUrl,
                orderRequest,
                String.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        Orders savedOrder = orderRepository.findAll().get(0);
        assertEquals(TestDataFactory.SYS_TEST_PRODUCT_ID, savedOrder.getProductId());
        assertEquals(OrderStatus.CONFIRMED, savedOrder.getStatus());
    }

    @Test
    void shouldFailOrder_WhenStockIsInsufficient() {
        String orderUrl = "http://localhost:" + orderPort + "/order/placeOrder";
        OrderRequest orderRequest = TestDataFactory.createTestOrderRequest();
        orderRequest.setQuantity(120);

        ResponseEntity<String> response = restTemplate.postForEntity(
                orderUrl,
                orderRequest,
                String.class
        );

        assertNotNull(response.getBody());

        Orders failedOrder = orderRepository.findAll().stream()
                .filter(o -> o.getProductId().equals(TestDataFactory.SYS_TEST_PRODUCT_ID))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Failed order was not saved to DB"));

        assertEquals(OrderStatus.FAILED, failedOrder.getStatus());
    }

    @Test
    void shouldFailOrder_WhenProductDoesNotExist() {
        String orderUrl = "http://localhost:" + orderPort + "/order/placeOrder";

        OrderRequest orderRequest = TestDataFactory.createTestOrderRequest();
        orderRequest.setProductType("Electronics");

        ResponseEntity<String> response = restTemplate.postForEntity(
                orderUrl,
                orderRequest,
                String.class
        );

        assertTrue(response.getBody().contains("\"status\":\"FAILED\""));
        assertTrue(response.getBody().contains("\"message\":\"Insufficient Stock or Invalid Product\""));

        Orders failedOrder = orderRepository.findAll().stream()
                .filter(o -> o.getProductId().equals(TestDataFactory.SYS_TEST_PRODUCT_ID))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Failed order was not saved to DB"));

        assertEquals(OrderStatus.FAILED, failedOrder.getStatus());
    }

}
