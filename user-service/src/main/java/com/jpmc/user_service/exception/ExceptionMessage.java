package com.jpmc.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionMessage {
    private LocalDateTime timestamp;
    private String message;
    private int statusCode;
    private String error;
    private String path;
}

