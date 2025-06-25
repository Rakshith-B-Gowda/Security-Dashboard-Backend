package com.jpmc.auth_service.controller;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import com.jpmc.auth_service.exception.AuthenticationFailedException;
import com.jpmc.auth_service.exception.EmailAlreadyInUseException;
import com.jpmc.auth_service.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController  // REST API for authentication
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) throws AuthenticationFailedException {
        log.info("Received login request for email: {}", req.getEmail());
        AuthResponse resp = authService.login(req);                // may throw AuthenticationFailedException
        return ResponseEntity.ok(resp);                            // 200 + token
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest req) throws EmailAlreadyInUseException {
        log.info("Received signup request for email: {}", req.getEmail());
        String msg = authService.signup(req);                      // may throw EmailAlreadyInUseException
        return ResponseEntity.status(HttpStatus.CREATED).body(msg);// 201
    }
}
