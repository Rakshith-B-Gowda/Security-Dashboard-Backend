package com.jpmc.auth_service.services;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import com.jpmc.auth_service.enums.Roles;
import com.jpmc.auth_service.mapper.SignupRequestMapper;
import com.jpmc.auth_service.model.Users;
import com.jpmc.auth_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService{

    private UserRepository userRepository;

    private SignupRequestMapper signupRequestMapper;

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest req) {
        AuthResponse response = new AuthResponse();
        Users user = userRepository.findByEmail(req.getEmail());

        if (user == null || !user.getPassword().equals(req.getPassword())) {
            response.setResponse("Login failed: Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.setResponse("Login successful");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> signup(SignupRequest req) {
        Users user = signupRequestMapper.toUser(req);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("Sign-up Successful");
    }
}
