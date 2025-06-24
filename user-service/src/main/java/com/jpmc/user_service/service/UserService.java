package com.jpmc.user_service.service;


import com.jpmc.user_service.dto.UpdateByAdminDto;
import com.jpmc.user_service.dto.UserDto;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.entity.User;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    String updatePermission(UpdateByAdminDto updateByAdminDto) ;
    Permission getPermission(Long id);
    String sendRequestToAdmin(Long id, Permission permission);
    UserDto getUserByEmail(String email);
}
