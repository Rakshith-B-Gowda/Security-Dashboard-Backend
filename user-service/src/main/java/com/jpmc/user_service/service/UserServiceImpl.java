package com.jpmc.user_service.service;


import com.jpmc.user_service.dto.RequestToAdminDto;
import com.jpmc.user_service.dto.UpdateByAdminDto;
import com.jpmc.user_service.dto.UserDto;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.entity.User;
import com.jpmc.user_service.exception.UserNotFoundException;
import com.jpmc.user_service.mapper.AdminMapper;
import com.jpmc.user_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

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

    public String updatePermission(UpdateByAdminDto updateByAdminDto) {
        User user = userRepository.findByEmail(updateByAdminDto.getEmail());
        user.setPermission(Permission.valueOf(updateByAdminDto.getUpdateRole()));
        userRepository.save(user);
        return "Role Updated";
    }



    public Permission getPermission(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPermission();
    }

    public String sendRequestToAdmin(Long id, Permission permission) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
         RequestToAdminDto requestToAdminDto=AdminMapper.toAdmin(user, permission);

        if (permission == Permission.READ) {
            log.info("Read permission requested for ID: " + id);
            return "Read permission requested for ID: " + id;
        } else {
            log.info("Upload permission requested for ID: " + id);
            return "Upload permission requested for ID: " + id;
        }
    }

}
