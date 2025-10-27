package com.bookstore.authservice.data.mapper;

import com.bookstore.authservice.data.entity.User;
import com.bookstore.authservice.dto.UserResponse;
import com.bookstore.authservice.dto.UserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "email", target = "email"),
        @Mapping(source = "password", target = "password"),
        @Mapping(target = "role", constant = "ROLE_USER")
    })
    User toEntity(UserRequest userRequest);

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "email", target = "email"),
        @Mapping(source = "role", target = "role")
    })
    UserResponse toResponse(User user);
}
