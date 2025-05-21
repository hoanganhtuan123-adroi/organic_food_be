package com.javafood.server.repository;

import com.javafood.server.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReposity extends JpaRepository<ProductEntity, Integer> {
    Optional<ProductEntity> findByProductId(int id);

    @Query("SELECT p FROM com.javafood.server.entity.ProductEntity p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.discount LEFT JOIN FETCH p.images ")
    List<ProductEntity> findAllWithCategory();

    @Query("SELECT DISTINCT p FROM com.javafood.server.entity.ProductEntity p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.discount " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.productId = :productId")
    Optional<ProductEntity> findProductById(@Param("productId") Integer productId);
}
