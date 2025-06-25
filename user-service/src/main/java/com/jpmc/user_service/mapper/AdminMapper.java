package com.jpmc.user_service.mapper;

import com.jpmc.user_service.dto.RequestToAdminDto;
import com.jpmc.user_service.model.User;
import com.jpmc.user_service.enums.Permission;

public class AdminMapper {
    public static RequestToAdminDto toAdmin(User user,Permission permission) {
        RequestToAdminDto requestToAdminDto=new RequestToAdminDto();
        requestToAdminDto.setEmail(user.getEmail());
        requestToAdminDto.setRequestedRole(permission.name());
        user.setPermission(Permission.DEFAULT);
        return requestToAdminDto;
    }
}
