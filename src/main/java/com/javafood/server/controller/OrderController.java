package com.javafood.server.controller;

import com.javafood.server.dto.request.OrderRequest;
import com.javafood.server.dto.response.*;
import com.javafood.server.service.OrderService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping()
    ApiResponse<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        return ApiResponse.<OrderResponse>builder().code(201).message("Tao thanh cong").result(orderService.createOrder(orderRequest)).build();
    }

    @GetMapping()
    ApiResponse<Page<SimpleOrderResponse>> getOrders(@PageableDefault(size = 10, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SimpleOrderResponse> orders = orderService.getAllOrders(pageable);
        return ApiResponse.<Page<SimpleOrderResponse>>builder().code(200).message("Lấy danh sách thành công!").result(orders).build();
    }

    @GetMapping("/{orderId}")
    ApiResponse<OrderResponse> getDetailOrder(@PathVariable Integer orderId) {
        return ApiResponse.<OrderResponse>builder().code(200).message("Lấy thành công!").result(orderService.getOrderById(orderId)).build();
    }

    // API mới: Lấy danh sách đơn hàng theo ID người dùng
    @GetMapping("/user/{userId}")
    ApiResponse<List<OrderResponse>> getOrdersByUserId(@PathVariable Integer userId) {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ApiResponse.<List<OrderResponse>>builder()
                .code(200)
                .message("Lấy danh sách đơn hàng thành công!")
                .result(orders)
                .build();
    }


    @GetMapping("/reports/revenue")
    public ApiResponse<RevenueReport> getRevenueReport(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        RevenueReport report = orderService.getRevenueReport(startDateTime, endDateTime);
        return ApiResponse.<RevenueReport>builder()
                .code(200)
                .message("Báo cáo doanh thu")
                .result(report)
                .build();
    }

    @GetMapping("/reports/best-selling-products")
    public ApiResponse<List<BestSellingProduct>> getBestSellingProducts(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        List<BestSellingProduct> products = orderService.getBestSellingProducts(startDateTime, endDateTime, limit);
        return ApiResponse.<List<BestSellingProduct>>builder()
                .code(200)
                .message("Sản phẩm bán chạy")
                .result(products)
                .build();
    }
}
