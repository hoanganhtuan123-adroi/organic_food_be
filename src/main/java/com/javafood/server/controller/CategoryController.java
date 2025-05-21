package com.javafood.server.controller;

import com.javafood.server.dto.request.CategoryRequest;
import com.javafood.server.dto.response.ApiResponse;
import com.javafood.server.dto.response.CategoryResponse;
import com.javafood.server.exception.AppException;
import com.javafood.server.exception.ErrorCode;
import com.javafood.server.service.CategoryService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @PostMapping()
    ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request){
       ApiResponse<CategoryResponse> apiResponse = ApiResponse.<CategoryResponse>builder().code(201).result(categoryService.createCategory(request)).message("Tạo thành công!").build();
       return apiResponse;
    }

    @PutMapping("/{category_id}")
    ApiResponse<CategoryResponse> updateCategory(@PathVariable int category_id, @RequestBody CategoryRequest request){
        if(request.getCategoryName().isEmpty() || request.getDescription().isEmpty()){
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        ApiResponse<CategoryResponse> api = ApiResponse.<CategoryResponse>builder().code(201).result(categoryService.updateCategory(category_id, request)).build();
        return api;
    }

    @GetMapping()
    ApiResponse<List<CategoryResponse>> getAllCategory(){
        List<CategoryResponse> listCategories = categoryService.getAllCategory();
        return ApiResponse.<List<CategoryResponse>>builder().code(200).message("Lấy dữ liệu thành công").result(listCategories).build();
    }

    @GetMapping("/{category_id}")
    ApiResponse<List<CategoryResponse>> getDetailCategory(@PathVariable Integer category_id){
        List<CategoryResponse> detailCategory = categoryService.getDetailCategory(category_id);
        return ApiResponse.<List<CategoryResponse>>builder().code(200).message("Lấy dữ liệu thành công").result(detailCategory).build();
    }

    @DeleteMapping("/{category_id}")
    ApiResponse<Void> deleteCategory(@PathVariable Integer category_id){
        categoryService.deleteCategory(category_id);
        return ApiResponse.<Void>builder().code(200).message("Xoá thành công").build();
    }


}
