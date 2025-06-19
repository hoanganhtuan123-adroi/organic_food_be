package com.javafood.server.controller;

import com.javafood.server.dto.request.ProductRequest;
import com.javafood.server.dto.response.ApiResponse;
import com.javafood.server.dto.response.ProductResponse;
import com.javafood.server.service.ProductService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
     ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAllProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .code(200)
                .message("Lấy danh sách thành công!")
                .result(productService.getAllProducts())
                .build();
    }

    @GetMapping("/{productId:[0-9]+}")
    public ApiResponse<ProductResponse> getDetailProduct(@PathVariable Integer productId) {
        return ApiResponse.<ProductResponse>builder()
                .code(200)
                .message("Lấy danh sách thành công!")
                .result(productService.getProductsDetail(productId))
                .build();
    }

    @GetMapping("/pagination")
    public ApiResponse<Page<ProductResponse>> getProductsPagination(
            @RequestParam(defaultValue = "0") int pageNo, // Trang mặc định là 0
            @RequestParam(defaultValue = "10") int pageSize, // Kích thước trang mặc định là 10
            @RequestParam(defaultValue = "id") String sortBy, // Sắp xếp mặc định theo ID
            @RequestParam(defaultValue = "asc") String sortDir // Hướng sắp xếp mặc định là tăng dần
    ){
       return ApiResponse.<Page<ProductResponse>>builder()
               .code(200)
               .message("Lấy danh sách thành công")
               .result(productService.getProductPagination(pageNo, pageSize, sortBy, sortDir))
               .build();
    }

    @PutMapping("/{productId:[0-9]+}")
    ApiResponse<ProductResponse> updateProduct(@PathVariable Integer productId, @RequestParam("productName") String productName,
                                               @RequestParam("description") String description,
                                               @RequestParam("unit") String unit,
                                               @RequestParam("origin") String origin,
                                               @RequestParam("tags") String tags,
                                               @RequestParam("isActive") boolean isActive,
                                               @RequestParam("price") BigDecimal price,
                                               @RequestParam("categoryID") Integer categoryID,
                                               @RequestParam(value = "discountID", required = false) Integer discountId,
                                               @RequestPart(value = "image", required = false) List<MultipartFile> images
    ) throws IOException {
        log.info("price : " + price);
        log.info(String.valueOf(price instanceof BigDecimal));
        ProductRequest productRequest = ProductRequest.builder()
                .productName(productName)
                .description(description)
                .unit(unit)
                .origin(origin)
                .tags(tags)
                .isActive(isActive)
                .image(images)
                .price(price)
                .categoryId(categoryID)
                .discountId(discountId)
                .build();
        return ApiResponse.<ProductResponse>builder().code(201).message("Sửa sản phẩm thành công!").result(productService.updateProduct(productId, productRequest)).build();
    }

    @PostMapping()
    ApiResponse<ProductResponse> addProduct(@RequestParam("productName") String productName,
                                            @RequestParam("description") String description,
                                            @RequestParam("unit") String unit,
                                            @RequestParam("origin") String origin,
                                            @RequestParam("tags") String tags,
                                            @RequestParam("isActive") boolean isActive,
                                            @RequestParam("price") BigDecimal price,
                                            @RequestParam("categoryID") Integer categoryID,
                                            @RequestPart(value = "image", required = false) List<MultipartFile> images
                                            ) throws IOException {
        ProductRequest productRequest = ProductRequest.builder()
                .productName(productName)
                .description(description)
                .unit(unit)
                .origin(origin)
                .tags(tags)
                .categoryId(categoryID)
                .isActive(isActive)
                .image(images)
                .price(price)
                .build();
        return ApiResponse.<ProductResponse>builder().code(201).message("Thêm sản phẩm thành công!").result(productService.createProduct(productRequest)).build();
    }

    @DeleteMapping("/{productId:[0-9]+}")
    ApiResponse<Void> deleteProduct(@PathVariable Integer productId) {
        productService.deleteProduct(productId);
        return ApiResponse.<Void>builder().code(200).message("Xoá sản phẩm thành công!").build();
    }


    @GetMapping("/feature-product/pagination")
    public ApiResponse<Page<ProductResponse>> getProductsToClient(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "8") int pageSize,
            @RequestParam(defaultValue = "productId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ){
        return ApiResponse.<Page<ProductResponse>>builder()
                .code(200)
                .message("Lấy danh sách sản phẩm thành công")
                .result(productService.getProductToClient(pageNo, pageSize, sortBy, sortDir))
                .build();
    }

    @PutMapping("/active/{productId}")
    public ApiResponse<Void> activateProduct(@PathVariable Integer productId, @RequestBody ProductRequest productRequest) {
        productService.activeProduct(productId, productRequest);
        return ApiResponse.<Void>builder().code(200).build();
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<Page<ProductResponse>> getProductsByCategory(
            @RequestParam(defaultValue = "0") int pageNo, // Trang mặc định là 0
            @RequestParam(defaultValue = "10") int pageSize, // Kích thước trang mặc định là 10
            @RequestParam(defaultValue = "id") String sortBy, // Sắp xếp mặc định theo ID
            @RequestParam(defaultValue = "asc") String sortDir, // Hướng sắp xếp mặc định là tăng dần
            @PathVariable Integer categoryId
    ){
        return ApiResponse.<Page<ProductResponse>>builder()
                .code(200)
                .message("Lấy danh sách thành công")
                .result(productService.getProductByCategory(pageNo, pageSize, sortBy, sortDir, categoryId))
                .build();
    }
}
