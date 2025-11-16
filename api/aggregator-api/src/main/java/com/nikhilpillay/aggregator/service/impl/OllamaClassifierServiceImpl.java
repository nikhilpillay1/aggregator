package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.model.dto.TransactionClassification;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.service.TransactionClassifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OllamaClassifierServiceImpl implements TransactionClassifierService {

    private final ChatClient chat;

    @Override
    public TransactionCategory classify(String transactionDescription) {

        BeanOutputConverter<TransactionClassification> outputConverter =
                new BeanOutputConverter<>(TransactionClassification.class);

        String systemPrompt = """
            You are a bank transaction classifier. Analyze the transaction description
            and determine which category it belongs to. Be precise and consistent.
            
            Categories: ACCOMMODATION, UTILITIES, GROCERIES, SHOPPING, RESTAURANT, TRANSPORTATION, INSURANCE, HEALTHCARE, DEBT_PAYMENT, REFUND, ACCOUNT_FEE, GAMING, TRAVEL, STREAMING_SERVICE, AIRTIME, INCOME, INTEREST, INVESTMENT, TAXES, WITHDRAWAL, DEPOSIT, PAYMENT, EFT, OTHER
            
            CRITICAL: Respond with ONLY valid JSON on a SINGLE LINE with NO whitespace, newlines, or formatting.
            Do not include any explanatory text, preamble, comments, or backslashes.
            Start your response directly with the opening brace.
            
            {format}
            """;

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

        return Optional.ofNullable(response)
                .map(outputConverter::convert)
                .map(TransactionClassification::category)
                .orElse(TransactionCategory.OTHER);
    }
}
