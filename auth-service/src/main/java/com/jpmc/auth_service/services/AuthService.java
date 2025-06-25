package com.jpmc.auth_service.services;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import com.jpmc.auth_service.exception.AuthenticationFailedException;
import com.jpmc.auth_service.exception.EmailAlreadyInUseException;

// AuthService interface defines the contract for authentication operations
public interface AuthService {
    // attempts login, returns 200+token or 401
    AuthResponse login(AuthRequest req) throws AuthenticationFailedException;

    // attempts signup, returns 201 or 400
    String signup(SignupRequest req) throws EmailAlreadyInUseException;
}
