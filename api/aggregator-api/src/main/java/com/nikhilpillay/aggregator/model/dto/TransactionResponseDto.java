package com.nikhilpillay.aggregator.model.dto;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponseDto(
        long id,
        LocalDate date,
        String description,
        BigDecimal amount,
        TransactionCategory category,
        String source,
        long customerId
) {}
