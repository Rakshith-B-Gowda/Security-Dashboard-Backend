package com.jpmc.auth_service.controller;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import com.jpmc.auth_service.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        log.info("Received login request for email: {}", req.getEmail());
        return authService.login(req);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest req) {
        log.info("Received signup request for email: {}", req.getEmail());
        return authService.signup(req);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("Test endpoint hit");
        return ResponseEntity.ok("Auth Service is running");
    }
}
