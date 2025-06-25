package com.jpmc.auth_service.exception;

public class EmailAlreadyInUseException extends Exception {
    public EmailAlreadyInUseException(String email) {
        super("Email already in use: " + email);
    }
}
