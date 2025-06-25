package com.jpmc.user_service.dto;

import com.jpmc.user_service.enums.Permission;
import lombok.Data;

@Data
public class UserDtoWithId {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Permission permission;
}
