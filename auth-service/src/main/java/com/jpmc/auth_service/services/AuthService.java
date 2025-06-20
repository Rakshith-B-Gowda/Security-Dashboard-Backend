package com.jpmc.auth_service.services;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


public interface AuthService {

    ResponseEntity<AuthResponse> login(AuthRequest req);

    ResponseEntity<String> signup(SignupRequest req);
}
