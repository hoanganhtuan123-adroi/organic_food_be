package com.javafood.server.configuration;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary configKey() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dedgvckly");
        config.put("api_key", "227225749911774");
        config.put("api_secret", "D3t-8Oyp4kyAhYqJxJTJF1zEnc8");
        return new Cloudinary(config);
    }
}
