package com.jpmc.auth_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice   // intercepts exceptions across all controllers
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<ErrorMessage> handleEmailConflict(EmailAlreadyInUseException ex, HttpServletRequest req) {
        log.warn("Signup error: {}", ex.getMessage());
        ErrorMessage body = new ErrorMessage(LocalDateTime.now(),
                                             HttpStatus.BAD_REQUEST.value(),
                                             HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                             ex.getMessage(),
                                             req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorMessage> handleAuthFail(AuthenticationFailedException ex, HttpServletRequest req) {
        log.warn("Authentication failed");
        ErrorMessage body = new ErrorMessage(LocalDateTime.now(),
                                             HttpStatus.UNAUTHORIZED.value(),
                                             HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                                             ex.getMessage(),
                                             req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorMessage> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String msg = "Invalid parameter '" + ex.getName() + "': expected " + ex.getRequiredType().getSimpleName();
        log.warn("Type mismatch: {}", msg);
        ErrorMessage body = new ErrorMessage(LocalDateTime.now(),
                                             HttpStatus.BAD_REQUEST.value(),
                                             HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                             msg,
                                             req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessage> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String cause = ex.getMostSpecificCause().getMessage();
        log.warn("Malformed JSON: {}", cause);
        ErrorMessage body = new ErrorMessage(LocalDateTime.now(),
                                             HttpStatus.BAD_REQUEST.value(),
                                             HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                             "Malformed JSON request: " + cause,
                                             req.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleAll(Exception ex, HttpServletRequest req) {
        log.error("Unexpected error", ex);
        ErrorMessage body = new ErrorMessage(LocalDateTime.now(),
                                             HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                             HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                             "Unexpected error: " + ex.getMessage(),
                                             req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
