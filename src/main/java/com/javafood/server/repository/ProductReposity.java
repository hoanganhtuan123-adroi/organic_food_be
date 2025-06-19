package com.javafood.server.repository;

import com.javafood.server.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReposity extends JpaRepository<ProductEntity, Integer> {
    Boolean existsByProductId(Integer productId);
    Boolean existsByProductName(String name);

    Optional<ProductEntity> findByProductId(int id);

    @Query("SELECT p FROM Products p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.discount LEFT JOIN FETCH p.images ")
    List<ProductEntity> findAllWithCategory();

    @Query(value = "SELECT DISTINCT p FROM Products p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.discount " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.isActive = true",
            countQuery = "SELECT count(p) FROM Products p WHERE p.isActive = true")
    Page<ProductEntity> getProductsToClient(Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Products p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.discount " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.isActive = true AND p.category.categoryId = :categoryID",
            countQuery = "SELECT count(p) FROM Products p WHERE p.isActive = true AND p.category.categoryId = :categoryID")
    Page<ProductEntity> getProductsByCategory(Pageable pageable, @Param("categoryID") int categoryID);

    @Query("SELECT DISTINCT p FROM Products p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.discount " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.productId = :productId")
    Optional<ProductEntity> findProductById(@Param("productId") Integer productId);

    @Query(value = "SELECT p FROM Products p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.discount LEFT JOIN FETCH p.images",
            countQuery = "SELECT count(p) FROM com.javafood.server.entity.ProductEntity p") // Thêm countQuery để Spring biết cách đếm tổng số phần tử
    Page<ProductEntity> getProductsWithPagination(Pageable pageable);
}
