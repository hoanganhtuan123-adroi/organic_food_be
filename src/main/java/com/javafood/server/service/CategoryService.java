package com.javafood.server.service;

import com.javafood.server.dto.request.CategoryRequest;
import com.javafood.server.dto.response.CategoryResponse;
import com.javafood.server.entity.CategoryEntity;
import com.javafood.server.exception.AppException;
import com.javafood.server.exception.ErrorCode;
import com.javafood.server.mapper.CategoryMapper;
import com.javafood.server.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CategoryMapper categoryMapper;

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if(categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())){
            throw new AppException(ErrorCode.EXISTS_DATA);
        }
        CategoryEntity categoryEntity = categoryMapper.toCategoryEntity(categoryRequest);
        categoryRepository.save(categoryEntity);
        return categoryMapper.toCategoryResponse(categoryEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public CategoryResponse updateCategory(Integer category_id, CategoryRequest categoryRequest) {
        if( !categoryRepository.existsByCategoryId(category_id)){
            throw new AppException(ErrorCode.NOT_EXISTS_DATA);
        } else if(categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())){
            throw new AppException(ErrorCode.EXISTS_DATA);
        }
        CategoryEntity category = categoryRepository.findByCategoryId(category_id);

        category.setCategoryName(categoryRequest.getCategoryName());
        category.setDescription(categoryRequest.getDescription());

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<CategoryResponse> getAllCategory(){
        return categoryRepository.findAll().stream().map((item)-> categoryMapper.toCategoryResponse(item)).toList() ;
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<CategoryResponse> getDetailCategory(Integer category_id){
        List<CategoryResponse> detailCategory = null;
        if(categoryRepository.existsByCategoryId(category_id)){
            detailCategory = categoryRepository.findById(category_id).stream().map((item)-> categoryMapper.toCategoryResponse(item)).toList() ;
        } else {
            throw new AppException(ErrorCode.NOT_EXISTS_DATA);
        }
        return detailCategory;
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public void deleteCategory(Integer category_id){
        if(!categoryRepository.existsByCategoryId(category_id)) throw new AppException(ErrorCode.NOT_EXISTS_DATA);
        categoryRepository.deleteById(category_id);
    }

}
