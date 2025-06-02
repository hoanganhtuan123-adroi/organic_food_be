package com.javafood.server.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    @NotNull
    Integer userId;
    LocalDateTime orderDate;
    BigDecimal subtotalAmount;
    String shippingAddress;
    String shippingMethod;
    BigDecimal shippingFee;
    BigDecimal finalAmount;
    String status;
    @NotEmpty
    private List<OrderDetailRequest> orderDetails;
    private List<PaymentRequest> payments; // Thêm danh sách thanh toán
}
