package com.nikhilpillay.aggregator.model.dto;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.model.enums.TransactionSource;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponseDto(
        long id,
        LocalDate date,
        String description,
        BigDecimal amount,
        TransactionSource source,
        TransactionCategory category,
        long customerId
) {}
