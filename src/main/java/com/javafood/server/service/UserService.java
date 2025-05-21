package com.javafood.server.service;

import com.javafood.server.dto.request.UserRequest;
import com.javafood.server.dto.response.UserResponse;
import com.javafood.server.entity.UserEntity;
import com.javafood.server.enums.Role;
import com.javafood.server.exception.AppException;
import com.javafood.server.exception.ErrorCode;
import com.javafood.server.mapper.UserMapper;
import com.javafood.server.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UserService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;

    public UserResponse addUser(UserRequest userRequest) {
        log.info("Adding user: " + userRequest.getUsername());
        log.info("Adding user: " + userRepository.existsByUsername(userRequest.getUsername()));
        if(userRepository.existsByUsername(userRequest.getUsername())) { throw new AppException(ErrorCode.USERNAME_EXISTED); }

        String hash_password = passwordEncoder.encode(userRequest.getPassword());

        UserEntity userEntity = userMapper.toUserEntity(userRequest);
        userEntity.setPassword(hash_password);

        userEntity.setRole("USER");

        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<UserResponse> getAllUser(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

//    @PostAuthorize("returnObject.username == authentication.name"
    public UserResponse updateUser(String id, UserRequest userRequest) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not Found"));

        userMapper.updateUser(userEntity, userRequest);

        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    public UserResponse getMyUser(){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        UserEntity byUsername = userRepository.findByUsername(username).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(byUsername);
    }

}
