package com.javafood.server.controller;

import com.javafood.server.dto.request.StockRequest;
import com.javafood.server.dto.request.StockTransactionRequest;
import com.javafood.server.dto.response.ApiResponse;
import com.javafood.server.dto.response.StockResponse;
import com.javafood.server.dto.response.StockTransactionResponse;
import com.javafood.server.service.StockService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StockController {

    @Autowired
    StockService stockService;

    @PostMapping()
    ApiResponse<Boolean> createStockAndTransaction(@RequestBody StockTransactionRequest transactionRequest) {
        return ApiResponse.<Boolean>builder().code(201).result(stockService.createStockAndTransaction(transactionRequest)).build();
    }

    @GetMapping()
    ApiResponse<List<StockResponse>> getAllStocks() {
        return ApiResponse.<List<StockResponse>>builder().code(200).result(stockService.getAllStocks()).message("Lấy danh sách thành công").build();
    }

    @GetMapping("/transaction")
    ApiResponse<Page<StockTransactionResponse>> getAllTransactionsPagination(@RequestParam(defaultValue = "0") int pageNo, // Trang mặc định là 0
                                                                             @RequestParam(defaultValue = "10") int pageSize, // Kích thước trang mặc định là 10
                                                                             @RequestParam(defaultValue = "transactionId") String sortBy, // Sắp xếp mặc định theo ID
                                                                             @RequestParam(defaultValue = "asc") String sortDir)// Hướng sắp xếp mặc định là tăng dần)
    {
        return ApiResponse.<Page<StockTransactionResponse>>builder().code(200).message("Lấy danh sách thành công!").result(stockService.getTransactionPagination(pageNo, pageSize,sortBy, sortDir)).build();
    }

}
