package com.example.userservice.exception;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(message);
    }
}
