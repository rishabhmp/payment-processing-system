package com.example.paymentservice.service.impl;

import com.example.paymentservice.dto.PaymentRequest;
import com.example.paymentservice.dto.PaymentResponse;
import com.example.paymentservice.entity.PaymentTransaction;
import com.example.paymentservice.exception.PaymentException;
import com.example.paymentservice.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.exception.ApiException;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentConfirmParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Captor
    private ArgumentCaptor<PaymentTransaction> transactionCaptor;

    @BeforeEach
    void setUp() {
        // Needed if Stripe static methods are called (we'll mock PaymentIntent below)
    }

    @Test
    void testProcessPayment_Success() throws StripeException {
        // Arrange
        PaymentRequest request = new PaymentRequest();
        request.setAmount(5000L); // $50
        request.setCurrency("USD");
        request.setCustomerEmail("test@example.com");

        PaymentIntent paymentIntentMock = mock(PaymentIntent.class);
        when(paymentIntentMock.getId()).thenReturn("pi_test_123");
        when(paymentIntentMock.getStatus()).thenReturn("succeeded");

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            mockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                        .thenReturn(paymentIntentMock);

            when(paymentIntentMock.confirm(any(PaymentIntentConfirmParams.class)))
                    .thenReturn(paymentIntentMock);

            // Act
            PaymentResponse response = paymentService.processPayment(request);

            // Assert
            assertEquals("pi_test_123", response.getTransactionId());
            assertEquals("succeeded", response.getStatus());
            assertEquals("Payment processed successfully.", response.getMessage());

            verify(paymentRepository).save(transactionCaptor.capture());
            PaymentTransaction savedTxn = transactionCaptor.getValue();
            assertEquals("test@example.com", savedTxn.getCustomerEmail());
        }
    }

    @Test
    void testProcessPayment_TooLowAmount_ThrowsException() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(4999L); // Just below $50
        request.setCurrency("USD");
        request.setCustomerEmail("user@example.com");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> paymentService.processPayment(request));

        assertTrue(ex.getMessage().contains("Amount must be at least 50 USD"));
    }

    @Test
    void testProcessPayment_TooHighAmount_ThrowsException() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(50100L); // Over $500
        request.setCurrency("USD");
        request.setCustomerEmail("user@example.com");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> paymentService.processPayment(request));

        assertTrue(ex.getMessage().contains("Amount exceeds the maximum limit"));
    }

    @Test
    void testProcessPayment_NonUsdCurrency_ThrowsException() {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(5000L);
        request.setCurrency("EUR");
        request.setCustomerEmail("user@example.com");

        PaymentException ex = assertThrows(PaymentException.class,
                () -> paymentService.processPayment(request));

        assertTrue(ex.getMessage().contains("Currency must be USD"));
    }

    @Test
    void testProcessPayment_StripeException_ThrowsPaymentException() throws StripeException {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(5000L);
        request.setCurrency("USD");
        request.setCustomerEmail("user@example.com");

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            mockedStatic.when(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)))
                        .thenThrow(new ApiException("Stripe error", null, null, 400, null));

            PaymentException ex = assertThrows(PaymentException.class,
                    () -> paymentService.processPayment(request));

            assertTrue(ex.getMessage().contains("Stripe error"));
        }
    }
}