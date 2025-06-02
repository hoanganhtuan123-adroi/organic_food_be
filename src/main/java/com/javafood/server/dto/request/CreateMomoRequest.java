package com.javafood.server.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateMomoRequest {
    String partnerCode;
    String requestType;
    String ipnUrl;
    String orderId;
    long amount;
    String requestId;
    String orderInfo;
    String redirectUrl;
    String lang;
    String extraData;
    String signature;
}
