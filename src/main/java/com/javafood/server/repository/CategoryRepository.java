package com.javafood.server.repository;

import com.javafood.server.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
    boolean existsByCategoryName(String categoryName);
    boolean existsByCategoryId(Integer categoryId);
    CategoryEntity findByCategoryId(Integer categoryId);
}
