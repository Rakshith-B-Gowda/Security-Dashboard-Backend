package com.jpmc.user_service.service;


import com.jpmc.user_service.entity.User;

import java.util.List;

public interface UserServiceInterface {

    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
}
