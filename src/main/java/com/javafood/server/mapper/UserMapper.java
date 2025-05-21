package com.javafood.server.mapper;

import com.javafood.server.dto.request.UserRequest;
import com.javafood.server.dto.response.UserResponse;
import com.javafood.server.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toUserEntity(UserRequest userRequest);

    UserResponse toUserResponse(UserEntity userEntity);

    void updateUser(@MappingTarget UserEntity userEntity, UserRequest userRequest);
}
