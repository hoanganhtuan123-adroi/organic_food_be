package com.javafood.server.service;

import com.javafood.server.dto.request.StockTransactionRequest;
import com.javafood.server.dto.response.StockResponse;
import com.javafood.server.dto.response.StockTransactionResponse;
import com.javafood.server.entity.ProductEntity;
import com.javafood.server.entity.StockEntity;
import com.javafood.server.entity.StockTransactionEntity;
import com.javafood.server.mapper.StockMapper;
import com.javafood.server.mapper.StockTransactionMapper;
import com.javafood.server.repository.ProductReposity;
import com.javafood.server.repository.StockRepository;
import com.javafood.server.repository.StockTransactionRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class StockService {
    final static String adminRole = "hasAuthority('SCOPE_ADMIN')";

    @Autowired
    StockRepository stockRepository;

    @Autowired
    StockTransactionRepository stockTransactionRepository;

    @Autowired
    ProductReposity productReposity;

    @Autowired
    StockMapper stockMapper;

    @Autowired
    StockTransactionMapper stockTransactionMapper;

    @Transactional
    @PreAuthorize(adminRole)
    public boolean createStockAndTransaction(StockTransactionRequest transactionRequest) {
        try {
            StockTransactionEntity stockTransactionEntity = stockTransactionMapper.toStockTransactionEntity(transactionRequest);

            ProductEntity product = productReposity.findByProductId(transactionRequest.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
            stockTransactionEntity.setProduct(product);
            StockTransactionEntity savedStockTransaction = stockTransactionRepository.save(stockTransactionEntity);

            Optional<StockEntity> existingStock = stockRepository.findByProduct_ProductId(transactionRequest.getProductId());

            if (existingStock.isPresent()) {
                updateExistingStock(existingStock.get(), savedStockTransaction, transactionRequest);
            } else {
                createNewStock(savedStockTransaction, transactionRequest);
            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create stock and transaction: " + e.getMessage(), e);
        }
    }

    private void updateExistingStock(StockEntity existingStock, StockTransactionEntity transaction, StockTransactionRequest request) {
        int currentQuantity = existingStock.getQuantity();
        int newQuantity = calculateNewQuantity(currentQuantity, request.getQuantity(), request.getTransactionType());

        // Kiểm tra số lượng không được âm
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock quantity. Current: " + currentQuantity + ", Requested: " + request.getQuantity());
        }

        existingStock.setQuantity(newQuantity);
        existingStock.setStockTransaction(transaction);
        stockRepository.save(existingStock);
    }

    private void createNewStock(StockTransactionEntity transaction, StockTransactionRequest request) {
        // Chỉ cho phép tạo stock mới khi là IMPORT hoặc ADJUSTMENT với số lượng dương
        if (request.getTransactionType().equals("EXPORT")) {
            throw new RuntimeException("Cannot export from non-existing stock");
        }

        StockEntity newStock = StockEntity.builder().stockTransaction(transaction).product(transaction.getProduct()).quantity(request.getQuantity()).build();

        stockRepository.save(newStock);
    }

    private int calculateNewQuantity(int currentQuantity, int transactionQuantity, String transactionType) {
        switch (transactionType.toUpperCase()) {
            case "IMPORT":
                return currentQuantity + transactionQuantity;
            case "EXPORT":
                return currentQuantity - transactionQuantity;
            case "ADJUSTMENT":
                // ADJUSTMENT có thể là tăng hoặc giảm tùy theo giá trị transactionQuantity
                // Nếu quantity > 0 thì tăng, < 0 thì giảm
                return currentQuantity + transactionQuantity;
            default:
                throw new RuntimeException("Unknown transaction type: " + transactionType);
        }
    }

    @PreAuthorize(adminRole)
    public List<StockResponse> getAllStocks() {
        return stockRepository.findAllStockAndProduct();
    }

    @PreAuthorize(adminRole)
    public Page<StockTransactionResponse> getTransactionPagination(int pageNo, int pageSize, String sortBy, String sortDir){
        try {
            log.info("Phân trang giao dịch - pageNo: {}, pageSize: {}, sortBy: {}, sortDir: {}", pageNo, pageSize, sortBy, sortDir);
            Pageable pageable = PageRequest.of(pageNo, pageSize, sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
            Page<StockTransactionEntity> transactionPagination = stockTransactionRepository.findAll(pageable);
            return transactionPagination.map(stockTransactionMapper::toStockTransactionResponse);
        } catch (RuntimeException e) {
            log.error("Lỗi khi phân trang giao dịch: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
