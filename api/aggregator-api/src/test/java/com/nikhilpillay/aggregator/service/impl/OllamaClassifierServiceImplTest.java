package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OllamaClassifierServiceImplTest {

    @Mock
    private ChatClient chat;

    @Mock
    private ChatClient.ChatClientRequestSpec promptSpec;

    @Mock
    private ChatClient.ChatClientRequestSpec systemSpec;

    @Mock
    private ChatClient.ChatClientRequestSpec userSpec;

    @Mock
    private ChatClient.CallResponseSpec callSpec;

    @InjectMocks
    private OllamaClassifierServiceImpl classifierService;

    @Test
    void classify_shouldReturnCorrectCategory() {
        // Given
        String description = "Woolworths grocery shopping";
        String jsonResponse = "{\"category\":\"GROCERIES\"}";

        when(chat.prompt()).thenReturn(promptSpec);
        when(promptSpec.system(any(Consumer.class))).thenReturn(systemSpec);
        when(systemSpec.user(description)).thenReturn(userSpec);
        when(userSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn(jsonResponse);

        // When
        TransactionCategory result = classifierService.classify(description);

        // Then
        assertEquals(TransactionCategory.GROCERIES, result);
    }

    @Test
    void classify_shouldReturnOtherOnException() {
        // Given
        String description = "Some transaction";

        when(chat.prompt()).thenThrow(new RuntimeException("AI service down"));

        // When
        TransactionCategory result = classifierService.classify(description);

        // Then
        assertEquals(TransactionCategory.OTHER, result);
    }

    @Test
    void classify_shouldReturnOtherOnNullResponse() {
        // Given
        String description = "Some transaction";

        when(chat.prompt()).thenReturn(promptSpec);
        when(promptSpec.system(any(Consumer.class))).thenReturn(systemSpec);
        when(systemSpec.user(description)).thenReturn(userSpec);
        when(userSpec.call()).thenReturn(callSpec);
        when(callSpec.content()).thenReturn(null);

        // When
        TransactionCategory result = classifierService.classify(description);

        // Then
        assertEquals(TransactionCategory.OTHER, result);
    }
}