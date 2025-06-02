package com.javafood.server.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailRequest {
    Integer productId;
    Integer quantity;
    BigDecimal originalPrice;
    BigDecimal finalPrice;
}
