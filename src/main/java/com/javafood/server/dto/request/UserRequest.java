package com.javafood.server.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String username;
    String password;
}
