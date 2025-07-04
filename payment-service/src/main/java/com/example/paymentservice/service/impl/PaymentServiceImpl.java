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
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentMethodCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private static final long MIN_AMOUNT = 50_00;
    private static final long MAX_AMOUNT = 500_00;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            if(request.getAmount()<MIN_AMOUNT){
                throw new PaymentException("Amount must be at least 50 USD.");
            }
            if(request.getAmount()>MAX_AMOUNT){
                throw new PaymentException("Amount exceeds the maximum limit of 999 USD.");
            }
            if(!"USD".equalsIgnoreCase(request.getCurrency())){
                throw new PaymentException("Currency must be USD");
            }
            // Step 1: Create PaymentIntent
            PaymentIntentCreateParams intentParams = PaymentIntentCreateParams.builder()
            .setAmount(request.getAmount())
            .setCurrency(request.getCurrency())
            .setReceiptEmail(request.getCustomerEmail())
            .setDescription("Payment for order")
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                    .setEnabled(true)
                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                    .build()
            )
            .build();


            PaymentIntent paymentIntent = PaymentIntent.create(intentParams);

            // Step 2: Confirm the PaymentIntent using test payment method directly
            PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                    .setPaymentMethod("pm_card_visa") // test Visa card method from Stripe
                    .build();

            paymentIntent = paymentIntent.confirm(confirmParams);

            // Step 3: Save transaction to DB
            PaymentTransaction txn = PaymentTransaction.builder()
                    .stripeTransactionId(paymentIntent.getId())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .customerEmail(request.getCustomerEmail())
                    .status(paymentIntent.getStatus())
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(txn);

            return new PaymentResponse(
                    paymentIntent.getId(),
                    paymentIntent.getStatus(),
                    "Payment processed successfully."
            );

        } catch (StripeException e) {
            throw new PaymentException("Stripe error: " + e.getMessage());
        }
    }
}
