package com.javafood.server.controller;

import com.javafood.server.dto.response.ApiResponse;
import com.javafood.server.entity.Email;
import com.javafood.server.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/emails")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping()
    public ApiResponse<Void> sendEmail(@Valid @RequestBody Email email) {
        emailService.sendEmail(email);
        return ApiResponse.<Void>builder().code(200).message("Thanh cong").build();
    }
}
