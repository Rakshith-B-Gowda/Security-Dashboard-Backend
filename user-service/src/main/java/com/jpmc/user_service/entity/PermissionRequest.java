package com.jpmc.user_service.entity;

import com.jpmc.user_service.enums.Permission;
import com.jpmc.user_service.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "permission_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private Permission permission;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
}
