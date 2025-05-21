package com.javafood.server.repository;

import com.javafood.server.entity.ImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Images i WHERE i.product.productId = :productId")
    void deleteByProductProductId(@Param("productId") Integer productId);

    List<ImageEntity> findByProductProductId(Integer productId);
}
