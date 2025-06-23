package com.jpmc.user_service.service;

import com.jpmc.user_service.dto.NotificationDto;
import com.jpmc.user_service.dto.RequestToAdminDto;
import com.jpmc.user_service.dto.UpdateByAdminDto;
import com.jpmc.user_service.entity.Notification;
import com.jpmc.user_service.entity.User;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.exception.UserNotFoundException;
import com.jpmc.user_service.mapper.AdminMapper;
import com.jpmc.user_service.mapper.UserMapper;
import com.jpmc.user_service.repository.NotificationRepository;
import com.jpmc.user_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final WebClient webClient;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public String updatePermission(UpdateByAdminDto updateByAdminDto) {
        User user = userRepository.findByEmail(updateByAdminDto.getEmail());
        user.setPermission(Permission.valueOf(updateByAdminDto.getUpdateRole()));
        userRepository.save(user);
        return "Role Updated";
    }

    @Override
    public Permission getPermission(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPermission();
    }

    @Override
    public String sendRequestToAdmin(Long id, Permission permission) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RequestToAdminDto dto = AdminMapper.toAdmin(user, permission);

        String response = webClient.post()
                .uri("http://localhost:9092/admin/requests/addrequest")
                .body(Mono.just(dto), RequestToAdminDto.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("Permission request sent for ID: {} with response: {}", id, response);
        return response;
    }

    public void sendInAppNotification(NotificationDto dto) {
        Notification notification = new Notification();
        notification.setEmail(dto.getEmail());
        notification.setMessage(dto.getMessage());
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForUser(String email) {
        return notificationRepository.findByEmailOrderByTimestampDesc(email);
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldNotifications() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByTimestampBefore(thirtyDaysAgo);
        log.info("Old notifications deleted before {}", thirtyDaysAgo);
    }
}
