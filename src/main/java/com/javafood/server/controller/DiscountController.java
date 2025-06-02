package com.javafood.server.controller;

import com.javafood.server.dto.request.DiscountActiveRequest;
import com.javafood.server.dto.request.DiscountRequest;
import com.javafood.server.dto.response.ApiResponse;
import com.javafood.server.dto.response.DiscountResponse;
import com.javafood.server.service.DiscountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/discounts")
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @PostMapping()
    ApiResponse<DiscountResponse> createDiscount(@RequestBody DiscountRequest discountRequest) {
        return ApiResponse.<DiscountResponse>builder().code(201).message("Tao thanh cong!").result(discountService.createDiscount(discountRequest)).build();
    }

    @PutMapping("/{discountId}")
    ApiResponse<DiscountResponse> updateDiscount(@PathVariable Integer discountId, @RequestBody @Valid DiscountRequest discountRequest) {

        return ApiResponse.<DiscountResponse>builder()
                .code(201)
                .message("Cập nhập thành công!")
                .result(discountService.updateDiscount(discountId, discountRequest))
                .build();
    }

    @GetMapping()
    ApiResponse<List<DiscountResponse>> getAllDiscounts() {
        ;
        return ApiResponse.<List<DiscountResponse>>builder().code(200).message("Lấy danh sách thành công!").result(discountService.getDiscount()).build();
    }

    @DeleteMapping("/delete/{discountId}")
    ApiResponse<Void> deleteDiscount(@PathVariable Integer discountId) {
        discountService.deleteDiscount(discountId);
        return ApiResponse.<Void>builder().code(200).message("Xoá thành công!").build();
    }

    @PutMapping("/active/{discountId}")
    ApiResponse<DiscountResponse> activeDiscount(@PathVariable Integer discountId, @RequestBody DiscountActiveRequest discountActiveRequest) {

        return ApiResponse.<DiscountResponse>builder()
                .code(200)
                .message("Cập nhập thành công!")
                .result(discountService.activeDiscount(discountId, discountActiveRequest))
                .build();
    }


}
