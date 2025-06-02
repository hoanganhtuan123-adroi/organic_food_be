package com.javafood.server.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartRequest {
    Integer cartId;
    Integer userId;
    Integer productId;
    Integer quantity;
}
