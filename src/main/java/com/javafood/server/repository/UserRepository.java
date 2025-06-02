package com.javafood.server.repository;

import com.javafood.server.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    boolean existsByUsername(String s);
    boolean existsById(Integer i);
    Optional<UserEntity> findById(Integer id);

    Optional<UserEntity> findByUsername(String id);

    @Query(value = "SELECT a FROM com.javafood.server.entity.UserEntity a WHERE a.role != 'ADMIN' AND a.firstName != '' AND a.lastName != '' ",
            countQuery = "SELECT count(a) FROM com.javafood.server.entity.UserEntity a  WHERE a.role != 'ADMIN' AND a.firstName != '' AND a.lastName != '' ") // Thêm countQuery để Spring biết cách đếm tổng số phần tử
    Page<UserEntity> getUsersWithPagination(Pageable pageable);
}
