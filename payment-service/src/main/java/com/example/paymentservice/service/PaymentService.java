package com.example.paymentservice.service;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request, String customerEmail);
}
