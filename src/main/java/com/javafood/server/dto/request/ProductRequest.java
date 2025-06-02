package com.javafood.server.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    Integer categoryId;
    Integer productId;
    Integer discountId;
    String productName;
    String description;
    String unit;
    String origin;
    String tags;
    BigDecimal price;
    List<MultipartFile> image;
    Boolean isActive;
}
