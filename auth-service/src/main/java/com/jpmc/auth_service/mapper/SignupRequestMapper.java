package com.jpmc.auth_service.mapper;

import com.jpmc.auth_service.dto.SignupRequest;
import com.jpmc.auth_service.enums.Roles;
import com.jpmc.auth_service.model.Users;
import org.springframework.stereotype.Component;

@Component
public class SignupRequestMapper {

     static public Users toUser(SignupRequest request) {
        Users user = new Users();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        Roles assignedRole = request.getRoles() != null ? Roles.ADMIN : Roles.USER;
        user.setRoles(assignedRole);
        return user;
    }
}
