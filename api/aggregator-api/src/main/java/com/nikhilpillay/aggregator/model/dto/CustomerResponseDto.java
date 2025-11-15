package com.nikhilpillay.aggregator.model.dto;

import java.util.List;

public record CustomerResponseDto(
        long id,
        String name,
        List<TransactionRequestDto> transactions
) {}
