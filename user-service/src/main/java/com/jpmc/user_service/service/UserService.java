package com.jpmc.user_service.service;

import com.jpmc.user_service.dto.UserDto;
import com.jpmc.user_service.entity.User;
import com.jpmc.user_service.exception.UserNotFoundException;
import com.jpmc.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService implements UserServiceInterface{
    @Autowired
    UserRepository repo;
    public User createUser(User user) {
        return repo.save(user);
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
