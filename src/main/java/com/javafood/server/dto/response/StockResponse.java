package com.javafood.server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockResponse {
     Integer stockId;
     Integer quantity;
     BigDecimal price; // Giả sử price là BigDecimal
     Integer productId;
     String productName;
     String unit;
}
