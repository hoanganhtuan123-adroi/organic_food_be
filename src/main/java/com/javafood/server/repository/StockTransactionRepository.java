package com.javafood.server.repository;

import com.javafood.server.entity.StockTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransactionEntity, Integer> {
    @Override
    Page<StockTransactionEntity> findAll(Pageable pageable);
}
