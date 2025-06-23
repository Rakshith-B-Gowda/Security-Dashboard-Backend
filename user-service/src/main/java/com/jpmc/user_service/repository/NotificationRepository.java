package com.jpmc.user_service.repository;

import com.jpmc.user_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByEmailOrderByTimestampDesc(String email);
    void deleteByTimestampBefore(LocalDateTime expiryDate);

}
