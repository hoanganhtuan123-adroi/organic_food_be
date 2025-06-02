package com.javafood.server.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    String paymentMethod;
    LocalDateTime paymentDate;
    String transactionId;
    BigDecimal amount;
    String transactionStatus;

}
