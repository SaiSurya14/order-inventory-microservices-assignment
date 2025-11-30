package com.koerber.assignment.order.mapper;

import com.koerber.assignment.order.dto.InventoryUpdateRequest;
import com.koerber.assignment.order.dto.OrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "productType", source = "productType")
    @Mapping(target = "quantity", source = "quantity")
    InventoryUpdateRequest toInventoryUpdateRequest(OrderRequest orderRequest);
}
