package com.javafood.server.mapper;

import com.javafood.server.dto.response.CartResponse;
import com.javafood.server.entity.CartEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ProductMapper.class)
public interface CartMapper {

    @Mapping(source = "cartId", target = "cartId")
    @Mapping(source = "product", target = "product")
    CartResponse toCartResponse(CartEntity cartEntity);
}
