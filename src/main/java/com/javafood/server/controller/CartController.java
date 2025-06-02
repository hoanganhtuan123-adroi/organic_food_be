package com.javafood.server.controller;

import com.javafood.server.dto.request.CartRequest;
import com.javafood.server.dto.response.ApiResponse;
import com.javafood.server.dto.response.CartResponse;
import com.javafood.server.service.CartService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @PostMapping
    ApiResponse<String> addToCart(@RequestBody CartRequest cart) {
        cartService.addToCart(cart);
        return ApiResponse.<String>builder().code(201).message("Thêm giỏ hang thành công").build();
    };

    @GetMapping("/user/{userId}")
    public ApiResponse<List<CartResponse>> getCartByUserId(@PathVariable("userId") Integer userId) {
        List<CartResponse> cartResponses = cartService.getCartByUserId(userId);
        if (cartResponses.isEmpty()) {
            return ApiResponse.<List<CartResponse>>builder()
                    .code(200)
                    .message("Không tìm thấy giỏ hàng cho người dùng")
                    .result(Collections.emptyList())
                    .build();
        }
        return ApiResponse.<List<CartResponse>>builder()
                .code(200)
                .message("Lấy giỏ hàng thành công")
                .result(cartResponses)
                .build();
    }


    @DeleteMapping("/delete/{cartId}")
    ApiResponse<Void> deleteFromCart(@PathVariable("cartId") Integer cartId) {
        cartService.deleteCart(cartId);
        return ApiResponse.<Void>builder().code(200).message("Xoá khỏi giỏ hàng thành công").build();
    }

    @PutMapping("/update/quantity")
    ApiResponse<Void> updateQuantity(@RequestBody CartRequest cart) {
        cartService.updateQuantity(cart);
        return ApiResponse.<Void>builder().code(200).message("Thêm số lượng thành công").build();
    }

}
