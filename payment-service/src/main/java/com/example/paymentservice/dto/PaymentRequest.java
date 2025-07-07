package com.example.paymentservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 5000, message = "Amount should be at least 50 USD")
    @Max(value = 50000, message = "Amount exceeds the maximum limit of 500 USD")
    private Long amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "USD", message = "Currency must be USD")
    private String currency;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Invalid email format")
    private String customerEmail;

    private String paymentMethodId;
}
