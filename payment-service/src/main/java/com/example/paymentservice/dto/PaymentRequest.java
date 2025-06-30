package com.example.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull
    private Long amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String customerEmail;
}
