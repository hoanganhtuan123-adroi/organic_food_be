package com.javafood.server.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity(name = "Users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String firstName;
    String lastName;
    @Column(unique=true)
    @Email(message = "INVALID_EMAIL")
    String email;
    String phone;
    String address;
    String username;
    @Size(min=8, message = "INVALID_PASSWORD")
    String password;
    String role;
    Timestamp created_at;
    Timestamp updated_at;

}
