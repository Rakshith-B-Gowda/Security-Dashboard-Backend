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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final WebClient.Builder webClientBuilder;

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest req) {
        log.info("Attempting login for email: {}", req.getEmail());
        Users user = userRepository.findByEmail(req.getEmail());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Login failed for email: {}", req.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse("Login failed"));
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles().name());
        log.info("Login successful for email: {}", req.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @Override
    public ResponseEntity<String> signup(SignupRequest req) {
        log.info("Attempting signup for email: {}", req.getEmail());

        Users existing = userRepository.findByEmail(req.getEmail());
        if (existing != null) {
            log.warn("Signup failed: Email already in use: {}", req.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
        }

        Users user = SignupRequestMapper.toUser(req);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);

        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setRole(user.getRoles().name());

        webClientBuilder.build()
                .post()
                .uri("http://localhost:9093/user/adduser")
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();

        log.info("Sign-up successful for email: {}", req.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body("Sign-up successful");
    }
}
