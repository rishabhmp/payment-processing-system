package com.example.userservice.exception;

// import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message);
    }
}
