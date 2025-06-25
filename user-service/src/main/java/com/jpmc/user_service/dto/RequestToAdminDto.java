package com.jpmc.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestToAdminDto {
    private String email;
    private String requestedRole;
    private Long permissionRequestId;
}
