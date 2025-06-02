package com.javafood.server.repository;

import com.javafood.server.entity.OrderDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Integer> {
    // Tìm tất cả OrderDetailEntity theo orderId của OrderEntity
    List<OrderDetailEntity> findByOrderOrderId(Integer orderId);

    @Query("SELECT od.product, SUM(od.quantity) FROM order_details od JOIN od.order o WHERE o.orderDate >= :startDate AND o.orderDate <= :endDate GROUP BY od.product ORDER BY SUM(od.quantity) DESC")
    List<Object[]> findBestSellingProducts(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
