package com.jpmc.auth_service.services;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    // attempts login, returns 200+token or 401
    ResponseEntity<AuthResponse> login(AuthRequest req);

    // attempts signup, returns 201 or 400
    ResponseEntity<String> signup(SignupRequest req);
}
