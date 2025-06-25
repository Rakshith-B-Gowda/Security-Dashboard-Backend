package com.jpmc.auth_service.services;

import com.jpmc.auth_service.dto.*;
import com.jpmc.auth_service.exception.AuthenticationFailedException;
import com.jpmc.auth_service.exception.EmailAlreadyInUseException;
import com.jpmc.auth_service.mapper.SignupRequestMapper;
import com.jpmc.auth_service.model.Users;
import com.jpmc.auth_service.repository.UserRepository;
import com.jpmc.auth_service.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public AuthResponse login(AuthRequest req) throws AuthenticationFailedException {
        log.info("Attempting login for email: {}", req.getEmail());
        Users user = userRepository.findByEmail(req.getEmail());          // lookup user
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Login failed for email: {}", req.getEmail());
            throw new AuthenticationFailedException();
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles().name()); // generate JWT
        log.info("Login successful for email: {}", req.getEmail());
        return new AuthResponse(token);
    }

    @Override
    public String signup(SignupRequest req) throws EmailAlreadyInUseException {
        log.info("Attempting signup for email: {}", req.getEmail());
        if (userRepository.findByEmail(req.getEmail()) != null) {
            log.warn("Signup failed, email exists: {}", req.getEmail());
            throw new EmailAlreadyInUseException(req.getEmail());
        }
        Users user = SignupRequestMapper.toUser(req);                     // map DTO to entity
        user.setPassword(passwordEncoder.encode(req.getPassword()));      // encode password
        userRepository.save(user);                                        // persist user
        log.info("User saved with email: {}", user.getEmail());

        UserDto userDto = new UserDto();                                  // build notification DTO
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setRole(user.getRoles().name());
        webClientBuilder.build().post().uri("http://localhost:9093/user/adduser")
                .bodyValue(userDto).retrieve().bodyToMono(Void.class).subscribe(); // async call
        log.info("User-service notified for email: {}", user.getEmail());

        return "Sign-up successful";
    }
}
