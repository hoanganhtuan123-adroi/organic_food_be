package com.javafood.server.repository;

import com.javafood.server.entity.CartEntity;
import com.javafood.server.entity.ProductEntity;
import com.javafood.server.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Integer> {
    Optional<CartEntity> findByUserAndProduct(UserEntity user, ProductEntity product);

    @Query("SELECT c FROM Carts c JOIN FETCH c.product WHERE c.user.id = :userId")
    List<CartEntity> findCartByUserId(@Param("userId") Integer userId);
}
