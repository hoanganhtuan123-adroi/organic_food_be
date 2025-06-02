package com.javafood.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderResponse {
     Integer orderId;
     Integer userId;
     String userName; // Thêm trường userName
     LocalDateTime orderDate;
     BigDecimal subtotalAmount;
     String shippingAddress;
     String shippingMethod;
     BigDecimal shippingFee;
     BigDecimal finalAmount;
     String status;
     LocalDateTime createDate;
     LocalDateTime updateDate;
     List<OrderDetailResponse> orderDetails;
     List<PaymentResponse> payments;
}
