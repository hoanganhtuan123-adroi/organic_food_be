package com.javafood.server.mapper;

import com.javafood.server.dto.request.StockTransactionRequest;
import com.javafood.server.dto.response.StockTransactionResponse;
import com.javafood.server.entity.StockTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StockTransactionMapper {

    StockTransactionEntity toStockTransactionEntity(StockTransactionRequest request);

    @Mapping(source = "product.productName", target = "productName")
    StockTransactionResponse toStockTransactionResponse(StockTransactionEntity entity);

}
