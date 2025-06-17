package com.jpmc.auth_service.services;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import com.jpmc.auth_service.enums.Roles;
import com.jpmc.auth_service.mapper.SignupRequestMapper;
import com.jpmc.auth_service.model.Users;
import com.jpmc.auth_service.repository.UserRepository;
import com.jpmc.auth_service.security.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest req) {
        Users user = userRepository.findByEmail(req.getEmail());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Login failed"));
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles().name());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Override
    public ResponseEntity<String> signup(SignupRequest req) {
        Users existing = userRepository.findByEmail(req.getEmail());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already in use");
        }
        Users user = SignupRequestMapper.toUser(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Sign-up successful");
    }
}
