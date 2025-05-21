package com.javafood.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity(name = "Discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DiscountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer discountId;
    @NotEmpty(message="INPUT_EMPTY")
    String code;
    @NotNull(message="INPUT_EMPTY")
    BigDecimal discountValue;
    @NotNull(message="INPUT_EMPTY")
    Date startDate;
    @NotNull(message="INPUT_EMPTY")
    Date endDate;
    Boolean active;
    Timestamp createdAt;
    Timestamp updatedAt;
}
