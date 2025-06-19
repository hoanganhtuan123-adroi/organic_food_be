package com.javafood.server.dto.request;

import lombok.Data;

@Data
public class MomoRequest {
    private String partnerCode;
    private String accessKey;
    private String requestId;
    private String amount;
    private String orderId;
    private String orderInfo;
    private String returnUrl;
    private String notifyUrl;
    private String extraData;
    private String requestType;
    private String signature;
    private String lang;
}
