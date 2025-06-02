package com.javafood.server.repository;

import com.javafood.server.dto.response.StockResponse;
import com.javafood.server.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, Integer> {
    Optional<StockEntity> findByProduct_ProductId(Integer productProductId);

    @Query("""
    SELECT new com.javafood.server.dto.response.StockResponse(
        s.stockId, s.quantity, p.price, p.productId, p.productName, p.unit
    )
    FROM com.javafood.server.entity.StockEntity s
    JOIN s.product p
""")
    List<StockResponse> findAllStockAndProduct();
}
