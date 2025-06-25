package com.jpmc.auth_service.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data                                    // generates getters/setters
@AllArgsConstructor                     // generates full-args constructor
@NoArgsConstructor                      // generates no-args constructor
public class ErrorMessage {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
