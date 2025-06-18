package com.jpmc.user_service.controller;

import com.jpmc.user_service.entity.User;
import com.jpmc.user_service.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@RestController
public class UserController {
   // @Autowired
    UserService userService;

    @PostMapping("/adduser")
    public ResponseEntity<User> create(@RequestBody User user) {
        User saved = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /* GET  /users  ── list all */
    @GetMapping("/all")
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /* GET  /users/{id}  ── single */
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


}
