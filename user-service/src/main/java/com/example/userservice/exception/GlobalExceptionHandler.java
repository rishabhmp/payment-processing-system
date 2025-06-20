package com.example.userservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        return errorResponse(message, 400);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getMessage())
                .findFirst()
                .orElse("Validation error");
        return errorResponse(message, 400);
    }

  @ExceptionHandler(ApiException.class)
public ResponseEntity<Object> handleApi(ApiException ex) {
    int status;
    if (ex instanceof BadRequestException) {
        status = 400;
    } else if (ex instanceof ConflictException) {
        status = 409;
    } else if (ex instanceof ForbiddenException) {
        status = 403;
    } else if (ex instanceof NotFoundException) {
        status = 404;
    } else {
        status = 400;
    }
    return errorResponse(ex.getMessage(), status);
}


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {
        log.error("Unhandled error", ex);
        return errorResponse("Internal server error", 500);
    }

    private ResponseEntity<Object> errorResponse(String message, int code) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("errorCode", code);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(code).body(body);
    }
}
