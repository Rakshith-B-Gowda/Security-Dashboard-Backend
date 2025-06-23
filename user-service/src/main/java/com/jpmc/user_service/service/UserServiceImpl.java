package com.jpmc.user_service.service;

import com.jpmc.user_service.dto.NotificationDto;
import com.jpmc.user_service.dto.RequestToAdminDto;
import com.jpmc.user_service.dto.UpdateByAdminDto;
import com.jpmc.user_service.entity.Notification;
import com.jpmc.user_service.entity.PermissionRequest;
import com.jpmc.user_service.entity.User;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.enums.RequestStatus;
import com.jpmc.user_service.exception.UserNotFoundException;
import com.jpmc.user_service.mapper.AdminMapper;
import com.jpmc.user_service.repository.NotificationRepository;
import com.jpmc.user_service.repository.PermissionRequestRepository;
import com.jpmc.user_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PermissionRequestRepository permissionRequestRepository;
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

        Permission currentPermission = user.getPermission();

        if (permission.ordinal() <= currentPermission.ordinal()) {
            throw new IllegalStateException("You already have this permission or higher: " + currentPermission);
        }

        // 1. Validate existing requests
        Optional<PermissionRequest> latestRequest = permissionRequestRepository
                .findTopByUserAndPermissionOrderByCreatedAtDesc(user, permission);

        if (latestRequest.isPresent()) {
            RequestStatus status = latestRequest.get().getStatus();
            if (status == RequestStatus.PENDING || status == RequestStatus.APPROVED) {
                throw new IllegalStateException("Request for " + permission + " is already " + status);
            }
        }

        // 2. Save new permission request and get generated ID
        PermissionRequest newRequest = new PermissionRequest();
        newRequest.setUser(user);
        newRequest.setPermission(permission);
        newRequest.setStatus(RequestStatus.PENDING);
        newRequest.setCreatedAt(LocalDateTime.now());

        PermissionRequest savedRequest = permissionRequestRepository.save(newRequest);

        // 3. Send request to admin-service with permissionRequestId
        RequestToAdminDto dto = new RequestToAdminDto();
        dto.setEmail(user.getEmail());
        dto.setRequestedRole(permission.name());
        dto.setPermissionRequestId(savedRequest.getId());

        String response = webClient.post()
                .uri("http://localhost:9092/admin/requests/addrequest")
                .body(Mono.just(dto), RequestToAdminDto.class)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("PermissionRequest ID {} sent to admin with response: {}", savedRequest.getId(), response);
        return "Permission request submitted.";
    }

    public List<PermissionRequest> getUserRequests(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return permissionRequestRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public String updateRequestStatus(Long requestId, RequestStatus status) {
        PermissionRequest request = permissionRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        request.setStatus(status);
        permissionRequestRepository.save(request);

        if (status == RequestStatus.APPROVED) {
            User user = request.getUser();
            user.setPermission(request.getPermission());
            userRepository.save(user);
        }

        return "Request status updated to " + status;
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
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByTimestampBefore(threshold);
        log.info("Old notifications deleted before {}", threshold);
    }
}
