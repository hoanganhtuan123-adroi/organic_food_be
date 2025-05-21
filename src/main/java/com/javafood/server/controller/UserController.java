package com.javafood.server.controller;

import com.javafood.server.dto.request.UserRequest;
import com.javafood.server.dto.response.ApiResponse;
import com.javafood.server.dto.response.UserResponse;
import com.javafood.server.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService userService;
    @PostMapping
    ApiResponse<UserResponse> addUser(@RequestBody @Valid UserRequest userRequest) {
        var users = userService.addUser(userRequest);
        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .code(201)
                .message("Success")
                .result(users)
                .build();

        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<UserResponse>> getAllUsers() {
       var authentication = SecurityContextHolder.getContext().getAuthentication();
       log.info("Username : " + authentication.getName());
       log.info("Roles : "+ authentication.getAuthorities());

        ApiResponse<List<UserResponse>> apiResponse = ApiResponse.<List<UserResponse>>builder()
                .code(200)
                .message("Success")
                .result(userService.getAllUser())
                .build();
        return apiResponse;
    }

    @PutMapping("/{id}")
    ApiResponse<UserResponse> updateUser(@PathVariable String id, @RequestBody UserRequest userRequest) {
        return ApiResponse.<UserResponse>builder().code(201).message("Update Success").result(userService.updateUser(id, userRequest)).build();
    }

    @GetMapping("/myinfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyUser())
                .build();
    }
}
