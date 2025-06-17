package com.example.userservice.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getDefaultMessage())
                .findFirst()
                .orElse("validation errors");
        return errorResponse(message, 400, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleValidation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getMessage())
                .findFirst()
                .orElse("validation errors");
        return errorResponse(message, 400, ex);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        return errorResponse(ex.getMessage(), 400, ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Object> handleForbidden(ForbiddenException ex) {
        return errorResponse(ex.getMessage(), 403, ex);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFound(NotFoundException ex) {
        return errorResponse(ex.getMessage(), 404, ex);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Object> handleConflict(ConflictException ex) {
        return errorResponse(ex.getMessage(), 409, ex);
    }

    @ExceptionHandler(Exception.class)
public ResponseEntity<Object> handleGeneric(Exception ex) {
    ex.printStackTrace();
    return errorResponse("internal server error", 500, ex);
}


    private ResponseEntity<Object> errorResponse(String message, int code, Exception ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("message", message);
    body.put("errorCode", code);
    body.put("timestamp", Instant.now().toString());

    if (ex != null && ex.getStackTrace().length > 0) {
        StackTraceElement origin = ex.getStackTrace()[0];
        body.put("source", origin.getClassName() + ":" + origin.getLineNumber());
    }

    return ResponseEntity.status(code).body(body);
}

}
