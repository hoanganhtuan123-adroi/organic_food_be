package com.javafood.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    Integer paymentId;
    Integer orderId;
    String paymentMethod;
    LocalDateTime paymentDate;
    String transactionId;
    BigDecimal amount;
    String transactionStatus;
    LocalDateTime createDate;
    LocalDateTime updateDate;
}
