package com.javafood.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SimpleOrderResponse {
    Integer orderId;
    String userName;
    LocalDateTime orderDate;
    BigDecimal finalAmount;
    String status;
    String paymentMethod;
}
