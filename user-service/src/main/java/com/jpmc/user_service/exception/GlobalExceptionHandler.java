package com.jpmc.user_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ExceptionMessage message = new ExceptionMessage(
            LocalDateTime.now(),
            ex.getMessage(),
            HttpStatus.NOT_FOUND.value(),
            "User Not Found",
            request.getDescription(false)
        );
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PermissionRequestException.class)
    public ResponseEntity<ExceptionMessage> handlePermissionRequestException(PermissionRequestException ex, WebRequest request) {
        ExceptionMessage message = new ExceptionMessage(
            LocalDateTime.now(),
            ex.getMessage(),
            HttpStatus.BAD_REQUEST.value(),
            "Permission Request Error",
            request.getDescription(false)
        );
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class, IllegalArgumentException.class})
    public ResponseEntity<ExceptionMessage> handleTypeMismatch(Exception ex, WebRequest request) {
        ExceptionMessage message = new ExceptionMessage(
            LocalDateTime.now(),
            "Invalid input or parameter type.",
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            request.getDescription(false)
        );
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handleGenericException(Exception ex, WebRequest request) {
        ExceptionMessage message = new ExceptionMessage(
            LocalDateTime.now(),
            ex.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            request.getDescription(false)
        );
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
