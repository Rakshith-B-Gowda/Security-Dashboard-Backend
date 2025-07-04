package com.jpmc.user_service.controller;

import com.jpmc.user_service.dto.NotificationDto;
import com.jpmc.user_service.model.Notification;
import com.jpmc.user_service.exception.PermissionRequestException;
import com.jpmc.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> createNotification(@RequestBody NotificationDto dto) {
        userService.sendInAppNotification(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{email}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String email) {
        return ResponseEntity.ok(userService.getNotificationsForUser(email));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<String> markAsRead(@PathVariable Long id) throws PermissionRequestException {
        userService.markAsRead(id);
        return ResponseEntity.ok("Notification marked as read");
    }
}
