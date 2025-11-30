package com.koerber.assignment.order;

import com.koerber.assignment.inventory.dto.InventoryUpdateTempRequest;
import com.koerber.assignment.inventory.entity.ProductType;
import com.koerber.assignment.order.dto.OrderRequest;

import java.time.LocalDate;
import java.util.List;

public final class TestDataFactory {

    public static final String SYS_TEST_PRODUCT_ID = "SYS-TEST-001";
    public static final String SYS_TEST_PRODUCT_NAME = "Apple";
    public static final String SYS_TEST_PRODUCT_TYPE = ProductType.FOOD.name();
    public static final int INITIAL_STOCK = 100;
    public static final int ORDER_QUANTITY = 10;

    /**
     * Creates OrderRequest for Integration Test.
     */
    public static OrderRequest createTestOrderRequest() {
        OrderRequest request = new OrderRequest();
        request.setProductId(SYS_TEST_PRODUCT_ID);
        request.setProductType(SYS_TEST_PRODUCT_TYPE);
        request.setQuantity(ORDER_QUANTITY);
        return request;
    }


    public static List<InventoryUpdateTempRequest> createInventoryLoad() {

        InventoryUpdateTempRequest.BatchData batch = new InventoryUpdateTempRequest.BatchData();
        batch.setQuantity(INITIAL_STOCK);
        batch.setExpiryDate(LocalDate.now().plusDays(10));

        InventoryUpdateTempRequest request = new InventoryUpdateTempRequest();
        request.setProductId(SYS_TEST_PRODUCT_ID);
        request.setProductName(SYS_TEST_PRODUCT_NAME);
        request.setProductType(com.koerber.assignment.inventory.entity.ProductType.valueOf(SYS_TEST_PRODUCT_TYPE));
        request.setBatches(List.of(batch));

        return List.of(request);
    }

}
