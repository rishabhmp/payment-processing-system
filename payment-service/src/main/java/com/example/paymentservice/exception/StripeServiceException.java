// src/main/java/com/example/paymentservice/exception/StripeServiceException.java
package com.example.paymentservice.exception;

public class StripeServiceException extends RuntimeException {
    public StripeServiceException(String message) {
        super(message);
    }

    public StripeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
