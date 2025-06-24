package com.jpmc.user_service.mapper;

import com.jpmc.user_service.dto.UserDto;
import com.jpmc.user_service.dto.UserDtoWithId;
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
    public static UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        return dto;
    }

    public static UserDtoWithId toDtoWithId(User user) {
        if (user == null) return null;
        UserDtoWithId dto = new UserDtoWithId();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        return dto;
    }
}
