package com.javafood.server.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name="order_details")
public class OrderDetailEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Integer orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", nullable = false)
    OrderEntity order;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    ProductEntity product;

    Integer quantity;
    BigDecimal originalPrice;
    BigDecimal finalPrice;

    @CreationTimestamp
    LocalDateTime createDate;
    @UpdateTimestamp
    LocalDateTime updateDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDetailEntity that = (OrderDetailEntity) o;
        return orderDetailId != null && orderDetailId.equals(that.orderDetailId);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
