package com.jpmc.user_service.dto;

import com.jpmc.user_service.enums.RequestStatus;
import lombok.Data;

@Data
public class RequestStatusUpdateDto {
    private Long requestId;
    private RequestStatus status;
}
