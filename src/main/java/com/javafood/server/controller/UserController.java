package com.javafood.server.controller;

import com.javafood.server.dto.request.UserRequest;
import com.javafood.server.dto.response.ApiResponse;
import com.javafood.server.dto.response.UserResponse;
import com.javafood.server.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUserById(@PathVariable Integer userId) {
        return ApiResponse.<UserResponse>builder()
                .code(200)
                .message("Lấy thành công!")
                .result(userService.getUserById(userId))
                .build();
    }

    @PutMapping("/{id:[0-9]+}")
    ApiResponse<UserResponse> updateUser(@PathVariable Integer id, @RequestBody UserRequest userRequest) {
        return ApiResponse.<UserResponse>builder().code(201).message("Update Success").result(userService.updateUser(id, userRequest)).build();
    }

    @GetMapping("/myinfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyUser())
                .build();
    }

    @GetMapping("/pagination")
    public ApiResponse<Page<UserResponse>> getProductsPagination(
            @RequestParam(defaultValue = "0") int pageNo, // Trang mặc định là 0
            @RequestParam(defaultValue = "10") int pageSize, // Kích thước trang mặc định là 10
            @RequestParam(defaultValue = "id") String sortBy, // Sắp xếp mặc định theo ID
            @RequestParam(defaultValue = "asc") String sortDir // Hướng sắp xếp mặc định là tăng dần
    ){
        return ApiResponse.<Page<UserResponse>>builder()
                .code(200)
                .message("Lấy danh sách thành công")
                .result(userService.getUsersPagination(pageNo, pageSize, sortBy, sortDir))
                .build();
    }

    @DeleteMapping("/{id:[0-9]+}")
    ApiResponse<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder().code(200).message("Xoá thành công!").build();
    }
}
