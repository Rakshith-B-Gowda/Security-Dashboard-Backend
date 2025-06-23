package com.jpmc.user_service.mapper;

import com.jpmc.user_service.dto.UserDto;
import com.jpmc.user_service.entity.User;
import com.jpmc.user_service.enums.Permission;

public class UserMapper {
    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setRole(dto.getRole());
        user.setPermission(dto.getRole().equalsIgnoreCase("ADMIN") ? Permission.READ_UPLOAD : Permission.DEFAULT);        return user;
    }
}
