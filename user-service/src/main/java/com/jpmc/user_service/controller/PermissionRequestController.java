package com.jpmc.user_service.controller;

import com.jpmc.user_service.dto.RequestStatusUpdateDto;
import com.jpmc.user_service.entity.PermissionRequest;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.enums.RequestStatus;
import com.jpmc.user_service.exception.PermissionRequestException;
import com.jpmc.user_service.exception.UserNotFoundException;
import com.jpmc.user_service.service.UserService;
import com.jpmc.user_service.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/requests")
@AllArgsConstructor
public class PermissionRequestController {

    private final UserService userService;


    // GET: Retrieve all requests made by a specific user
    @GetMapping("/{userId}")
    public ResponseEntity<List<PermissionRequest>> getRequests(@PathVariable Long userId) throws UserNotFoundException {
        List<PermissionRequest> requests = userService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }

    // PUT: Admin (or backend) updates the status of a specific request
    @PutMapping("/update-status")
    public ResponseEntity<String> updateRequestStatus(@RequestBody RequestStatusUpdateDto dto) throws PermissionRequestException, UserNotFoundException {
        String status = userService.updateRequestStatus(dto.getRequestId(), dto.getStatus());
        return ResponseEntity.ok(status);
    }
}
