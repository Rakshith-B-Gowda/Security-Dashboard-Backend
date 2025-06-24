package com.jpmc.user_service.controller;

import com.jpmc.user_service.dto.RequestStatusUpdateDto;
import com.jpmc.user_service.entity.PermissionRequest;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.enums.RequestStatus;
import com.jpmc.user_service.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/requests")
@RequiredArgsConstructor
public class PermissionRequestController {

    private final UserServiceImpl userService;


    // GET: Retrieve all requests made by a specific user
    @GetMapping("/{userId}")
    public ResponseEntity<List<PermissionRequest>> getRequests(@PathVariable Long userId) {
        List<PermissionRequest> requests = userService.getUserRequests(userId);
        return ResponseEntity.ok(requests);
    }

    // PUT: Admin (or backend) updates the status of a specific request
    @PutMapping("/update-status")
    public ResponseEntity<String> updateRequestStatus(@RequestBody RequestStatusUpdateDto dto) {
        String status = userService.updateRequestStatus(dto.getRequestId(), dto.getStatus());
        return ResponseEntity.ok(status);
    }
}
