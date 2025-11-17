package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.model.dto.TransactionClassification;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.service.TransactionClassifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OllamaClassifierServiceImpl implements TransactionClassifierService {

    private final ChatClient chat;

    private static final String CATEGORY_LIST;

    static {
        CATEGORY_LIST = Arrays.stream(TransactionCategory.values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    final BeanOutputConverter<TransactionClassification> outputConverter =
            new BeanOutputConverter<>(TransactionClassification.class);

    @Override
    public TransactionCategory classify(String transactionDescription) {

        String systemPrompt = """
            You are a bank transaction classifier. Analyze the transaction description
            and determine which category it belongs to. Be precise and consistent.
            
            Categories: %s
            
            CRITICAL: Respond with ONLY valid JSON on a SINGLE LINE with NO whitespace, newlines, or formatting.
            Do not include any explanatory text, preamble, comments, or backslashes.
            Start your response directly with the opening brace.
            
            {format}
            """.formatted(CATEGORY_LIST);

        String response;
        try {
            response = chat.prompt()
                    .system(sp -> sp.text(systemPrompt)
                            .param("format", outputConverter.getFormat()))
                    .user(transactionDescription)
                    .call()
                    .content();
        } catch (Exception e) {
            return TransactionCategory.OTHER;
        }

        try {
            return Optional.ofNullable(response)
                    .map(outputConverter::convert)
                    .map(TransactionClassification::category)
                    .orElse(TransactionCategory.OTHER);
        } catch (Exception e) {
            return TransactionCategory.OTHER;
        }
    }
}
