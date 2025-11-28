package com.koerber.assignment.order.mapper;

import com.koerber.assignment.order.dto.InventoryUpdateRequest;
import com.koerber.assignment.order.dto.OrderRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryUpdateRequest toInventoryUpdateRequest(OrderRequest orderRequest);
}
