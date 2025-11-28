package com.koerber.assignment.order.mapper;

import com.koerber.assignment.order.dto.OrderRequest;
import com.koerber.assignment.order.dto.OrderResponse;
import com.koerber.assignment.order.entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "failureReason", ignore = true)
    Orders toEntity(OrderRequest request);

    @Mapping(target = "orderId", source = "order.orderId")
    @Mapping(target = "status", source = "order.status")
    @Mapping(target = "message", source = "message")
    OrderResponse toResponse(Orders order, String message);
}
