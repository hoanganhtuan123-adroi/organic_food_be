package com.javafood.server.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    @NotBlank(message = "Email can not be blank")
    private String email;
    @NotBlank(message = "Subject can not be blank")
    private String subject;
    @NotBlank(message = "Email can not be blank")
    private String body;

}
