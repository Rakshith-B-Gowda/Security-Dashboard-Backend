package com.jpmc.user_service.service;

import com.jpmc.user_service.dto.*;
import com.jpmc.user_service.model.Notification;
import com.jpmc.user_service.model.PermissionRequest;
import com.jpmc.user_service.model.User;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.enums.RequestStatus;
import com.jpmc.user_service.exception.PermissionRequestException;
import com.jpmc.user_service.exception.UserNotFoundException;
import com.jpmc.user_service.mapper.UserMapper;
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
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        return UserMapper.toDto(saved);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.toDto(user);
    }

    @Override
    public String updatePermission(UpdateByAdminDto updateByAdminDto)
            throws UserNotFoundException {
        User user = userRepository.findByEmail(updateByAdminDto.getEmail());
        if (user == null) {
            throw new UserNotFoundException(
                    "User not found with email: " + updateByAdminDto.getEmail()
            );
        }
        user.setPermission(
                Permission.valueOf(updateByAdminDto.getUpdateRole())
        );
        userRepository.save(user);
        return "Role Updated";
    }

    @Override
    public Permission getPermission(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getPermission();
    }

    @Override
    public String sendRequestToAdmin(Long id, Permission permission)
            throws UserNotFoundException, PermissionRequestException {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 0. Prevent any new request if a PENDING request already exists
        boolean hasPending = permissionRequestRepository
                .existsByUserAndStatus(user, RequestStatus.PENDING);
        if (hasPending) {
            throw new PermissionRequestException(
                    "You already have a pending permission request. " +
                            "Please wait for it to be processed."
            );
        }

        // 1. Ensure user isn't requesting same-or-lower permission
        Permission currentPermission = user.getPermission();
        if (permission.ordinal() <= currentPermission.ordinal()) {
            throw new PermissionRequestException(
                    "You already have this permission or higher: " + currentPermission
            );
        }

        // 2. Optional per-permission duplicate check (still here for clarity)
        Optional<PermissionRequest> latestSame = permissionRequestRepository
                .findTopByUserAndPermissionOrderByCreatedAtDesc(user, permission);
        if (latestSame.isPresent()) {
            RequestStatus status = latestSame.get().getStatus();
            if (status == RequestStatus.PENDING || status == RequestStatus.APPROVED) {
                throw new PermissionRequestException(
                        "Request for " + permission + " is already " + status
                );
            }
        }

        // 3. Create and save the new permission request
        PermissionRequest newRequest = new PermissionRequest();
        newRequest.setUser(user);
        newRequest.setPermission(permission);
        newRequest.setStatus(RequestStatus.PENDING);
        newRequest.setCreatedAt(LocalDateTime.now());

        PermissionRequest savedRequest = permissionRequestRepository.save(newRequest);

        // 4. Forward to admin-service
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

        log.info(
                "PermissionRequest ID {} sent to admin with response: {}",
                savedRequest.getId(),
                response
        );
        return "Permission request submitted.";
    }

    @Override
    public List<PermissionRequest> getUserRequests(Long userId)
            throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with id: " + userId
                ));
        return permissionRequestRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public String updateRequestStatus(Long requestId, RequestStatus status)
            throws PermissionRequestException, UserNotFoundException {
        PermissionRequest req = permissionRequestRepository.findById(requestId)
                .orElseThrow(() -> new PermissionRequestException("Request not found"));

        req.setStatus(status);
        permissionRequestRepository.save(req);

        if (status == RequestStatus.APPROVED) {
            User user = req.getUser();
            if (user == null) {
                throw new UserNotFoundException("User not found for request");
            }
            Permission current = user.getPermission();
            Permission incoming = req.getPermission();

            // Only upgrade, never downgrade or reassign a lower/equal permission
            if (incoming.ordinal() > current.ordinal()) {
                user.setPermission(incoming);
                userRepository.save(user);
            }
        }

        return "Request status updated to " + status;
    }

    @Override
    public void sendInAppNotification(NotificationDto dto) {
        Notification notification = new Notification();
        notification.setEmail(dto.getEmail());
        notification.setMessage(dto.getMessage());
        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(String email) {
        return notificationRepository.findByEmailOrderByTimestampDesc(email);
    }

    @Override
    public void markAsRead(Long id) throws PermissionRequestException {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new PermissionRequestException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupOldNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        notificationRepository.deleteByTimestampBefore(threshold);
        log.info("Old notifications deleted before {}", threshold);
    }

    @Override
    public UserDtoWithId getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return UserMapper.toDtoWithId(user);
    }
}
