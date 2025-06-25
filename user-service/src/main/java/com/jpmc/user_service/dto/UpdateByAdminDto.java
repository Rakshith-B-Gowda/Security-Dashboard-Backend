package com.jpmc.user_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateByAdminDto {
    private String email;
    private String updateRole;
}
