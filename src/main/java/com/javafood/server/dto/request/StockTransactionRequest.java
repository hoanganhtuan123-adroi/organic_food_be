package com.javafood.server.dto.request;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransactionRequest {
    Integer productId;
    Integer quantity;
    BigDecimal price;
    String transactionType;
}
