package com.jpmc.admin_service.controller;

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

    @GetMapping
    public List<Admin> listPending() {
        return adminService.listPending();
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<Void> approve(@PathVariable Long id) {
        adminService.approve(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Void> reject(@PathVariable Long id) {
        adminService.reject(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}