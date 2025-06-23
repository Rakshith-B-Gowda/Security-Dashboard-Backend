package com.jpmc.admin_service.controller;

import com.jpmc.admin_service.dto.AddRequestDto;
import com.jpmc.admin_service.model.Admin;
import com.jpmc.admin_service.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/requests")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/addrequest")
    public ResponseEntity<Admin> addRequest(@RequestBody AddRequestDto addRequestDto){
        return ResponseEntity.ok(adminService.createSignupRequest(addRequestDto));
    }

    // Endpoint to view all pending signup requests (for admin dashboard)
    @GetMapping("/pending")
    public List<Admin> listPendingRequests() {
        return adminService.listPendingRequests();
    }

    // Endpoint to view ALL signup requests (pending, approved, rejected - for admin overview)
    @GetMapping("/all")
    public List<Admin> listAllRequests() {
        return adminService.listAllRequests();
    }

    // Endpoint for Admin to approve a specific signup request
    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approveRequest(@PathVariable Long id) {
        try {
            adminService.approveRequest(id);
            // Using HttpStatus.OK and a message for more descriptive success than 204 No Content
            return new ResponseEntity<>("Request ID " + id + " approved successfully.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict (e.g., already approved/rejected)
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }

    // Endpoint for Admin to reject a specific signup request
    @PutMapping("/reject/{id}")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        try {
            adminService.rejectRequest(id);
            // Using HttpStatus.OK and a message for more descriptive success than 204 No Content
            return new ResponseEntity<>("Request ID " + id + " rejected successfully.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // 409 Conflict
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }


}