package com.jpmc.user_service.service;


import com.jpmc.user_service.dto.NotificationDto;
import com.jpmc.user_service.dto.UpdateByAdminDto;
import com.jpmc.user_service.dto.UserDto;
import com.jpmc.user_service.dto.UserDtoWithId;
import com.jpmc.user_service.model.Notification;
import com.jpmc.user_service.model.PermissionRequest;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.enums.RequestStatus;
import com.jpmc.user_service.exception.PermissionRequestException;
import com.jpmc.user_service.exception.UserNotFoundException;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto) throws UserNotFoundException;

    List<UserDto> getAllUsers();

    UserDto getUserById(Long id) throws UserNotFoundException;

    String updatePermission(UpdateByAdminDto updateByAdminDto) throws UserNotFoundException;

    Permission getPermission(Long id) throws UserNotFoundException;

    String sendRequestToAdmin(Long id, Permission permission) throws UserNotFoundException, PermissionRequestException;

    UserDtoWithId getUserByEmail(String email) throws UserNotFoundException;

    void sendInAppNotification(NotificationDto dto);

    List<Notification> getNotificationsForUser(String email);

    List<PermissionRequest> getUserRequests(Long userId) throws UserNotFoundException;

    String updateRequestStatus(Long requestId, RequestStatus status) throws PermissionRequestException, UserNotFoundException;

    void markAsRead(Long id) throws PermissionRequestException;
}
