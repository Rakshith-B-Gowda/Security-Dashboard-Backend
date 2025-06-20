package com.jpmc.auth_service.dto;

import com.jpmc.auth_service.enums.Roles;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequest {
    private String name;
    public String email;
    public String password;
}
