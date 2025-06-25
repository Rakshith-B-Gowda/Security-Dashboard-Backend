package com.jpmc.user_service.controller;

import com.jpmc.user_service.dto.UpdateByAdminDto;
import com.jpmc.user_service.dto.UserDto;
import com.jpmc.user_service.dto.UserDtoWithId;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.exception.PermissionRequestException;
import com.jpmc.user_service.exception.UserNotFoundException;
import com.jpmc.user_service.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/adduser")
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDto) throws UserNotFoundException {
        UserDto createdUser = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> findById(@PathVariable Long id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    //send the webclient read request to admin
    @PostMapping("/{id}/request-read")
    public ResponseEntity<String> requestRead(@PathVariable Long id) throws UserNotFoundException, PermissionRequestException {
        userService.sendRequestToAdmin(id, Permission.READ);
        return ResponseEntity.ok("Read permission requested.");
    }
    //send the webclient update request to admin
    @PostMapping("/{id}/request-read/upload")
    public ResponseEntity<String> requestUpload(@PathVariable Long id) throws UserNotFoundException, PermissionRequestException {
        userService.sendRequestToAdmin(id, Permission.READ_UPLOAD);
        return ResponseEntity.ok("Upload permission requested.");
    }

    @GetMapping("/{id}/permission")
    public ResponseEntity<Permission> getStatus(@PathVariable Long id) throws UserNotFoundException {
        return ResponseEntity.ok(userService.getPermission(id));
    }
    //receive the update from the admin to change the permission
    @PostMapping("/admin/update")
    public ResponseEntity<String> adminUpdate(@RequestBody UpdateByAdminDto updateByAdminDto) throws UserNotFoundException {
        return ResponseEntity.ok(userService.updatePermission(updateByAdminDto));
    }

    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserDtoWithId> getUserByEmail(@PathVariable String email) throws UserNotFoundException {
        UserDtoWithId userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }
}
