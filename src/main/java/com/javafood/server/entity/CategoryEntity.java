package com.javafood.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.security.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "Categories")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer categoryId;
    String categoryName;
    String description;
    Timestamp createdAt;
    Timestamp updatedAt;
}
