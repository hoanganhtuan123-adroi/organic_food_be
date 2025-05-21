package com.javafood.server.mapper;

import com.javafood.server.dto.request.DiscountRequest;
import com.javafood.server.dto.response.DiscountResponse;
import com.javafood.server.entity.DiscountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DiscountMapper {
    DiscountEntity toDiscountEntity(DiscountRequest request);
    DiscountResponse toDiscountResponse(DiscountEntity entity);
}
