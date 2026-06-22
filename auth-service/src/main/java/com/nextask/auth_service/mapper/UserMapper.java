package com.nextask.auth_service.mapper;

import com.nextask.auth_service.dto.UserResponse;
import com.nextask.auth_service.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "username", source = "name")
    UserResponse toUserResponse(User user);
}
