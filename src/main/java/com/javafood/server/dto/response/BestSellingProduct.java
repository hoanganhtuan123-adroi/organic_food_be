package com.javafood.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BestSellingProduct {
    private Integer productId;
    private String productName;
    private Long totalQuantitySold;
}
