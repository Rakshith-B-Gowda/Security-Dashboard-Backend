package com.jpmc.auth_service.controller;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import com.jpmc.auth_service.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // marks this as a REST controller
@RequestMapping("/auth") // base path for auth endpoints
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // POST /auth/login : authenticates user and returns JWT
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        log.info("Received login request for email: {}", req.getEmail()); // log incoming
        ResponseEntity<AuthResponse> resp = authService.login(req);      // delegate
        log.info("Login response status: {}", resp.getStatusCode());     // log outcome
        return resp;
    }

    // POST /auth/signup : registers a new user
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest req) {
        log.info("Received signup request for email: {}", req.getEmail()); // log incoming
        ResponseEntity<String> resp = authService.signup(req);            // delegate
        log.info("Signup response status: {}", resp.getStatusCode());     // log outcome
        return resp;
    }
}
