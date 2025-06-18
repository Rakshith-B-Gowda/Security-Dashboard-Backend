package com.jpmc.user_service.service;


import com.jpmc.user_service.entity.Permission;
import com.jpmc.user_service.entity.User;
import com.jpmc.user_service.exception.UserNotFoundException;
import com.jpmc.user_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Data
@AllArgsConstructor
//@NoArgsConstructor
public class UserService implements UserServiceInterface{
    //@Autowired
    UserRepository userRepository;
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    public void updatePermission(Long id, Permission permission) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPermission(permission);
        userRepository.save(user);
    }

    public Permission getPermission(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPermission();
    }


}
