package com.javafood.server.repository;

import com.javafood.server.entity.DiscountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<DiscountEntity, Integer> {
    boolean existsByCode(String discountCode);
    DiscountEntity findByDiscountId(Integer discountId);
}
