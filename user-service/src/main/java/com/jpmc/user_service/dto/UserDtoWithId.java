package com.jpmc.user_service.dto;

import lombok.Data;

@Data
public class UserDtoWithId {
    private Long id;
    private String name;
    private String email;
    private String role;
}
