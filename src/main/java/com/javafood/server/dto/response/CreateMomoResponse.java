package com.javafood.server.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateMomoResponse {
    String partnerCode;
    String orderId;
    String requestId;
    long amount;
    long responseTime;
    String message;
    int resultCode;
    String payUrl;
    String deeplink;
    String qrCodeUrl;
}
