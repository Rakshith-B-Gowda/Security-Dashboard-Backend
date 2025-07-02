package com.jpmc.user_service.repository;

import com.jpmc.user_service.model.PermissionRequest;
import com.jpmc.user_service.model.User;
import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermissionRequestRepository extends JpaRepository<PermissionRequest, Long> {
    Optional<PermissionRequest> findTopByUserAndPermissionOrderByCreatedAtDesc(User user, Permission permission);
    List<PermissionRequest> findByUserOrderByCreatedAtDesc(User user);
    boolean existsByUserAndStatus(User user, RequestStatus status);
}
