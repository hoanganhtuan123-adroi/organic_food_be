package com.javafood.server.mapper;


import com.javafood.server.dto.request.ProductRequest;
import com.javafood.server.dto.response.CategoryResponse;
import com.javafood.server.dto.response.ProductResponse;
import com.javafood.server.entity.CategoryEntity;
import com.javafood.server.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    ProductEntity toProductEntity(ProductRequest request);

    @Mapping(source="discount", target = "discount")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "images", target = "images")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProductResponse toProductResponse(ProductEntity entity);

    // Thêm phương thức ánh xạ từ CategoryEntity sang CategoryResponse
    @Mapping(source = "categoryId", target = "categoryId")
    @Mapping(source = "categoryName", target = "categoryName")
    CategoryResponse toCategoryResponse(CategoryEntity category);


}
