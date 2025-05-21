package com.javafood.server.mapper;

import com.javafood.server.dto.request.CategoryRequest;
import com.javafood.server.dto.response.CategoryResponse;
import com.javafood.server.entity.CategoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toCategoryEntity(CategoryRequest request);
    CategoryResponse toCategoryResponse(CategoryEntity entity);

}
