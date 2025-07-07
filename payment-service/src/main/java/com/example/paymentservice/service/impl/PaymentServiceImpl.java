// src/main/java/com/example/paymentservice/service/impl/PaymentServiceImpl.java

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
import com.stripe.param.PaymentIntentConfirmParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse processPayment(PaymentRequest request, String customerEmail) {
        try {
            // Step 1: Create PaymentIntent
            PaymentIntent paymentIntent = createPaymentIntent(request, customerEmail);

            // Step 2: Confirm the PaymentIntent with a test card
            paymentIntent = confirmPaymentIntent(paymentIntent);

            // Step 3: Persist the transaction
            savePaymentTransaction(paymentIntent, request, customerEmail);

            // Step 4: Return success response
            return new PaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    "Payment processed successfully."
            );

        } catch (StripeException e) {
            throw new PaymentException("Stripe error: " + e.getMessage());
        }
    }

    private PaymentIntent createPaymentIntent(PaymentRequest request, String customerEmail) throws StripeException {
        PaymentIntentCreateParams intentParams = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())
                .setCurrency(request.getCurrency())
                .setReceiptEmail(customerEmail)
                .setDescription("Payment for order")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build()
                )
                .build();

        return PaymentIntent.create(intentParams);
    }

    private PaymentIntent confirmPaymentIntent(PaymentIntent paymentIntent) throws StripeException {
        PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                .setPaymentMethod("pm_card_visa") // Stripe test card
                .build();

        return paymentIntent.confirm(confirmParams);
    }

    private void savePaymentTransaction(PaymentIntent paymentIntent, PaymentRequest request, String customerEmail) {
        PaymentTransaction txn = PaymentTransaction.builder()
                .stripeTransactionId(paymentIntent.getId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .customerEmail(customerEmail)
                .status(paymentIntent.getStatus())
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(txn);
    }
}
