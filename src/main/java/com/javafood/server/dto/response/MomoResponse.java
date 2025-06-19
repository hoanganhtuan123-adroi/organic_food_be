package com.javafood.server.dto.response;

import lombok.Data;

@Data
public class MomoResponse {
    private int resultCode;
    private String message;
    private String payUrl;
    private String deeplink;
    private String qrCodeUrl;
}
