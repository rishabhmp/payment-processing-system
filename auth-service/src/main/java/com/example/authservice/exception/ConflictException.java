package com.example.authservice.exception;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message);
    }
}
