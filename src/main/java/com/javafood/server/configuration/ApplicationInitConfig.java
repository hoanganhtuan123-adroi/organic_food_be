package com.javafood.server.configuration;

import com.javafood.server.entity.UserEntity;
import com.javafood.server.enums.Role;
import com.javafood.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncode;

    @Bean
    ApplicationRunner runner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                UserEntity user = UserEntity.builder()
                        .role("ADMIN")
                        .username("admin")
                        .password(passwordEncode.encode("admin"))
                        .build();

                userRepository.save(user);
                log.warn("Account admin has been created");
            };
        };
    }
}

