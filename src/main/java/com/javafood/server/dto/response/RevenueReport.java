package com.javafood.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueReport {
    private BigDecimal totalRevenue;
    private Long numberOfOrders;
    private BigDecimal averageOrderValue;
}
