package com.jpmc.auth_service.services;

import com.jpmc.auth_service.dto.AuthRequest;
import com.jpmc.auth_service.dto.AuthResponse;
import com.jpmc.auth_service.dto.SignupRequest;
import com.jpmc.auth_service.dto.UserDto;
import com.jpmc.auth_service.mapper.SignupRequestMapper;
import com.jpmc.auth_service.model.Users;
import com.jpmc.auth_service.repository.UserRepository;
import com.jpmc.auth_service.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service  // bean for authentication logic
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final WebClient.Builder webClientBuilder;

    // login logic: validate creds and generate JWT
    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest req) {
        log.info("Attempting login for email: {}", req.getEmail());               // log attempt
        Users user = userRepository.findByEmail(req.getEmail());                 // fetch user
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Login failed for email: {}", req.getEmail());              // invalid creds
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Login failed"));         // 401
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles().name()); // create JWT
        log.info("Login successful for email: {}", req.getEmail());               // log success
        return ResponseEntity.ok(new AuthResponse(token));                        // return 200
    }

    // signup logic: create user and notify user-service
    @Override
    public ResponseEntity<String> signup(SignupRequest req) {
        log.info("Attempting signup for email: {}", req.getEmail());              // log attempt
        Users existing = userRepository.findByEmail(req.getEmail());              // check duplicate
        if (existing != null) {
            log.warn("Signup failed, email exists: {}", req.getEmail());          // duplicate
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email already in use");                  // 400
        }
        Users user = SignupRequestMapper.toUser(req);                             // map DTO→entity
        user.setPassword(passwordEncoder.encode(req.getPassword()));              // encode password
        userRepository.save(user);                                                // save user
        log.info("User saved with email: {}", user.getEmail());                   // log save

        UserDto userDto = new UserDto();                                          // build payload
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setRole(user.getRoles().name());
        webClientBuilder.build().post().uri("http://localhost:9093/user/adduser")
                .bodyValue(userDto).retrieve().bodyToMono(Void.class).subscribe();     // async notify
        log.info("Notification sent for signup email: {}", req.getEmail());       // log notify

        return ResponseEntity.status(HttpStatus.CREATED).body("Sign-up successful"); // 201
    }
}
