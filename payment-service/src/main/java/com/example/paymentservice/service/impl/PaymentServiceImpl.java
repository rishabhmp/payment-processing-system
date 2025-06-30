package com.example.paymentservice.service.impl;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.entity.PaymentTransaction;
import com.example.paymentservice.exception.PaymentException;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(request.getAmount())
                    .setCurrency(request.getCurrency())
                    .setReceiptEmail(request.getCustomerEmail())
                    .setDescription("Payment for order")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                    ).build();

            PaymentIntent intent = PaymentIntent.create(params);

            PaymentTransaction txn = PaymentTransaction.builder()
                    .stripeTransactionId(intent.getId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .customerEmail(request.getCustomerEmail())
                    .status(intent.getStatus())
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(txn);

            return new PaymentResponse(intent.getId(), intent.getStatus(), "Payment processed successfully.");
        } catch (StripeException e) {
            throw new PaymentException("Stripe error: " + e.getMessage());
        }
    }
}
