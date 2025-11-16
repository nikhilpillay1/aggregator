package com.nikhilpillay.aggregator.model.dto;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;

public record TransactionClassification(
        TransactionCategory category
) {}
