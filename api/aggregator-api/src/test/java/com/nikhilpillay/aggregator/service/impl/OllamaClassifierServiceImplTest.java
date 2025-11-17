package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OllamaClassifierServiceImplTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.ChatClientRequestSpec systemSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @InjectMocks
    private OllamaClassifierServiceImpl classifierService;

    @BeforeEach
    void setUp() {
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(any(Consumer.class))).thenReturn(systemSpec);
        when(systemSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
    }

    @Test
    void givenGroceryStoreDescription_thenTransactionClassifiedAsGroceries() {
        String description = "WOOLWORTHS GATEWAY";
        String jsonResponse = "{\"category\":\"GROCERIES\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.GROCERIES, result);
        verify(chatClient).prompt();
        verify(systemSpec).user(description);
    }

    @Test
    void givenRestaurantDescription_thenTransactionClassifiedAsRestaurant() {
        String description = "MCDONALDS DOWNTOWN";
        String jsonResponse = "{\"category\":\"RESTAURANT\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.RESTAURANT, result);
    }

    @Test
    void givenUtilityBillDescription_thenTransactionClassifiedAsUtilities() {
        String description = "ELECTRICITY PAYMENT ESKOM";
        String jsonResponse = "{\"category\":\"UTILITIES\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.UTILITIES, result);
    }

    @Test
    void givenTransportationDescription_thenTransactionClassifiedAsTransportation() {
        String description = "UBER TRIP 12345";
        String jsonResponse = "{\"category\":\"TRANSPORTATION\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.TRANSPORTATION, result);
    }

    @Test
    void givenStreamingServiceDescription_thenTransactionClassifiedAsStreamingService() {
        String description = "NETFLIX.COM SUBSCRIPTION";
        String jsonResponse = "{\"category\":\"STREAMING_SERVICE\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.STREAMING_SERVICE, result);
    }

    @Test
    void givenInsuranceDescription_thenTransactionClassifiedAsInsurance() {
        String description = "OLD MUTUAL LIFE INSURANCE";
        String jsonResponse = "{\"category\":\"INSURANCE\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.INSURANCE, result);
    }

    @Test
    void givenHealthcareDescription_thenTransactionClassifiedAsHealthcare() {
        String description = "DISCHEM PHARMACY";
        String jsonResponse = "{\"category\":\"HEALTHCARE\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.HEALTHCARE, result);
    }

    @Test
    void givenIncomeDescription_thenTransactionClassifiedAsIncome() {
        String description = "SALARY DEPOSIT COMPANY XYZ";
        String jsonResponse = "{\"category\":\"INCOME\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.INCOME, result);
    }

    @Test
    void givenChatClientThrowsException_thenTransactionClassifiedAsOther() {
        String description = "ANY DESCRIPTION";
        when(responseSpec.content()).thenThrow(new RuntimeException("API Error"));

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenNullResponse_thenTransactionClassifiedAsOther() {
        String description = "UNKNOWN TRANSACTION";
        when(responseSpec.content()).thenReturn(null);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenEmptyResponse_thenTransactionClassifiedAsOther() {
        String description = "UNKNOWN TRANSACTION";
        when(responseSpec.content()).thenReturn("");

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenInvalidJsonResponse_thenTransactionClassifiedAsOther() {
        String description = "SOME TRANSACTION";
        String invalidJson = "This is not JSON";
        when(responseSpec.content()).thenReturn(invalidJson);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenMalformedJsonResponse_thenTransactionClassifiedAsOther() {
        String description = "SOME TRANSACTION";
        String malformedJson = "{\"category\":GROCERIES}"; // Missing quotes
        when(responseSpec.content()).thenReturn(malformedJson);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenJsonWithoutCategoryField_thenTransactionClassifiedAsOther() {
        String description = "SOME TRANSACTION";
        String jsonResponse = "{\"type\":\"EXPENSE\"}"; // Wrong field name
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenJsonWithNullCategory_thenTransactionClassifiedAsOther() {
        String description = "SOME TRANSACTION";
        String jsonResponse = "{\"category\":null}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenJsonWithInvalidCategory_thenTransactionClassifiedAsOther() {
        String description = "SOME TRANSACTION";
        String jsonResponse = "{\"category\":\"INVALID_CATEGORY\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenAccommodationDescription_thenTransactionClassifiedAsAccommodation() {
        String description = "AIRBNB BOOKING CAPE TOWN";
        String jsonResponse = "{\"category\":\"ACCOMMODATION\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.ACCOMMODATION, result);
    }

    @Test
    void givenShoppingDescription_thenTransactionClassifiedAsShopping() {
        String description = "AMAZON ONLINE PURCHASE";
        String jsonResponse = "{\"category\":\"SHOPPING\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.SHOPPING, result);
    }

    @Test
    void givenRefundDescription_thenTransactionClassifiedAsRefund() {
        String description = "REFUND FROM STORE XYZ";
        String jsonResponse = "{\"category\":\"REFUND\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.REFUND, result);
    }

    @Test
    void givenDebtPaymentDescription_thenTransactionClassifiedAsDebtPayment() {
        String description = "CREDIT CARD PAYMENT";
        String jsonResponse = "{\"category\":\"DEBT_PAYMENT\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.DEBT_PAYMENT, result);
    }

    @Test
    void givenGamingDescription_thenTransactionClassifiedAsGaming() {
        String description = "STEAM STORE PURCHASE";
        String jsonResponse = "{\"category\":\"GAMING\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.GAMING, result);
    }

    @Test
    void givenAirtimeDescription_thenTransactionClassifiedAsAirtime() {
        String description = "VODACOM AIRTIME PURCHASE";
        String jsonResponse = "{\"category\":\"AIRTIME\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.AIRTIME, result);
    }

    @Test
    void givenVagueDescription_thenTransactionClassifiedAsOther() {
        String description = "PAYMENT";
        String jsonResponse = "{\"category\":\"OTHER\"}";
        when(responseSpec.content()).thenReturn(jsonResponse);

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void givenExceptionDuringCall_thenTransactionClassifiedAsOther() {
        String description = "SOME TRANSACTION";
        when(requestSpec.call()).thenThrow(new RuntimeException("Call error"));

        TransactionCategory result = classifierService.classify(description);

        assertEquals(TransactionCategory.OTHER, result);
    }
}