package com.javafood.server.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/momo")
public class MomoController {
    @Value("${momo.secretKey}")
    private String secretKey;

    @PostMapping("/notify")
    public String handleNotify(@RequestBody Map<String, Object> callbackData) throws Exception {
        // Lấy dữ liệu từ callback
        String orderId = (String) callbackData.get("orderId");
        String transId = (String) callbackData.get("transId");
        int resultCode = (int) callbackData.get("resultCode");
        String receivedSignature = (String) callbackData.get("signature");

        // Tạo chữ ký để xác thực
        String rawData = "partnerCode=" + callbackData.get("partnerCode") + "&accessKey=" + callbackData.get("accessKey") +
                "&requestId=" + callbackData.get("requestId") + "&orderId=" + orderId + "&transId=" + transId +
                "&amount=" + callbackData.get("amount") + "&resultCode=" + resultCode;
        String expectedSignature = hmacSHA256(rawData, secretKey);

        if (expectedSignature.equals(receivedSignature)) {
            if (resultCode == 0) {
                // Thanh toán thành công, cập nhật trạng thái đơn hàng
                return "Payment successful for order: " + orderId;
            } else {
                // Thanh toán thất bại
                return "Payment failed for order: " + orderId;
            }
        } else {
            return "Invalid signature";
        }
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder();
        for (byte b : rawHmac) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }


}
