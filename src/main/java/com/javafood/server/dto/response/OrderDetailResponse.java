package com.javafood.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {
     Integer orderDetailId;
     Integer orderId;
     Integer productId; // Chỉ lấy productId thay vì toàn bộ ProductEntity
     String productName;
     Integer quantity;
     BigDecimal originalPrice;
     BigDecimal finalPrice;
     LocalDateTime createDate;
     LocalDateTime updateDate;
}
