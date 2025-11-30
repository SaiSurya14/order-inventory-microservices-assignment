package com.koerber.assignment.inventory.mapper;

import com.koerber.assignment.inventory.dto.InventoryUpdateTempRequest;
import com.koerber.assignment.inventory.entity.Batch;
import com.koerber.assignment.inventory.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryLoadMapper {

    @Mapping(target = "id", ignore = true)
    Product toProduct(InventoryUpdateTempRequest request);

    Batch toBatch(InventoryUpdateTempRequest.BatchData batchData);

    List<Batch> toBatchList(List<InventoryUpdateTempRequest.BatchData> batchDataList);
}
