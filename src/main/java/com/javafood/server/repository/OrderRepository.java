package com.javafood.server.repository;

import com.javafood.server.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Integer> {
    @EntityGraph(attributePaths = {"user", "payments"})
    Page<OrderEntity> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderDetails.product", "payments"})
    Optional<OrderEntity> findByOrderId(Integer orderId);

    @EntityGraph(attributePaths = {"user", "orderDetails.product", "payments"})
    List<OrderEntity> findByUserId(Integer userId);

    @Query("SELECT SUM(o.finalAmount) FROM orders o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate")
    BigDecimal getTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM orders o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate")
    Long getNumberOfOrders(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
