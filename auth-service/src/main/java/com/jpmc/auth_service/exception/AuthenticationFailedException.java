package com.jpmc.auth_service.exception;

public class AuthenticationFailedException extends Exception {
    public AuthenticationFailedException() {
        super("Invalid email or password");
    }
}
