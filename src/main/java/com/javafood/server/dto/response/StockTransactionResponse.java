package com.javafood.server.dto.response;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransactionResponse {
    Integer transactionId;
    String productName;
    Integer quantity;
    BigDecimal price;
    String transactionType;
    LocalDateTime transactionDate;
}
