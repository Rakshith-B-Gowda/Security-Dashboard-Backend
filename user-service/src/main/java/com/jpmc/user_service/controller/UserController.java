package com.jpmc.user_service.controller;

import com.jpmc.user_service.dto.UpdateByAdminDto;
import com.jpmc.user_service.dto.UserDto;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.entity.User;
import com.jpmc.user_service.mapper.UserMapper;
import com.jpmc.user_service.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    UserServiceImpl userServiceImpl;

    @PostMapping("/adduser")
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) {
        UserDto createdUser = userServiceImpl.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userServiceImpl.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userServiceImpl.getUserById(id));
    }
    //send the webclient read request to admin
    @PostMapping("/{id}/request-read")
    public ResponseEntity<String> requestRead(@PathVariable Long id) {
        userServiceImpl.sendRequestToAdmin(id, Permission.READ);
        return ResponseEntity.ok("Read permission requested.");
    }
    //send the webclient update request to admin
    @PostMapping("/{id}/request-read/upload")
    public ResponseEntity<String> requestUpload(@PathVariable Long id) {
        userServiceImpl.sendRequestToAdmin(id, Permission.READ_UPLOAD);
        return ResponseEntity.ok("Upload permission requested.");
    }

    @GetMapping("/{id}/permission")
    public ResponseEntity<Permission> getStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userServiceImpl.getPermission(id));
    }
    //receive the update from the admin to change the permission
    @PostMapping("/admin/update")
    public ResponseEntity<String> adminUpdate(@RequestBody UpdateByAdminDto updateByAdminDto){
        return ResponseEntity.ok(userServiceImpl.updatePermission(updateByAdminDto));
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        UserDto userDto = userServiceImpl.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }
}
