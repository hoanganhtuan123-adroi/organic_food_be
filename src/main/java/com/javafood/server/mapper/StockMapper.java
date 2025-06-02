package com.javafood.server.mapper;

import com.javafood.server.dto.request.StockRequest;
import com.javafood.server.dto.response.StockResponse;
import com.javafood.server.entity.StockEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StockMapper {
    StockEntity toStockEntity(StockRequest stockRequest);
    StockResponse toStockResponse(StockEntity stockEntity);
}
