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
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class UserService {
    final static String adminRole = "hasAuthority('SCOPE_ADMIN')";
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserMapper userMapper;

    @PreAuthorize(adminRole)
    public UserResponse addUser(UserRequest userRequest) {
        log.info("Adding user: " + userRequest.getUsername());
        log.info("Adding user: " + userRepository.existsByUsername(userRequest.getUsername()));
        if(userRepository.existsByUsername(userRequest.getUsername())) { throw new AppException(ErrorCode.EXISTS_DATA); }

        String hash_password = passwordEncoder.encode(userRequest.getPassword());

        UserEntity userEntity = userMapper.toUserEntity(userRequest);
        userEntity.setPassword(hash_password);

        userEntity.setRole("USER");

        userRepository.save(userEntity);
        return userMapper.toUserResponse(userEntity);
    }

    @PreAuthorize(adminRole)
    public List<UserResponse> getAllUser(){
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize(adminRole)
    public UserResponse getUserById(Integer id) {
        return userRepository.findById(id).map(userMapper::toUserResponse).orElseThrow(() -> new AppException(ErrorCode.NOT_EXISTS_DATA));
    }

//    @PostAuthorize("returnObject.username == authentication.name"
    @PreAuthorize(adminRole)
    public UserResponse updateUser(Integer id, UserRequest userRequest) {
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

    @PreAuthorize(adminRole)
    public Page<UserResponse> getUsersPagination(int pageNo, int pageSize, String sortBy, String sortDir) {
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize, sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
            Page<UserEntity> usersPagination = userRepository.getUsersWithPagination(pageable);
            return usersPagination.map(userMapper::toUserResponse);
        } catch(Exception e){
            log.error("Lỗi khi lấy danh sách sản phẩm phân trang: ", e);
            throw e;
        }
    }

    @PreAuthorize(adminRole)
    public void deleteUser(Integer id) {
        if(!userRepository.existsById(id)){
            throw new AppException(ErrorCode.NOT_EXISTS_DATA);
        }
        userRepository.deleteById(id);
    }

}
