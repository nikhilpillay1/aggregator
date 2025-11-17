package com.nikhilpillay.aggregator.service;

import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.dto.TransactionRequestDto;
import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    List<TransactionResponseDto> importTransactionsFromCsv(MultipartFile file, Long customerId, String source) throws IOException;

    TransactionResponseDto findById(Long id);

    List<TransactionResponseDto> findAll();

    Page<Transaction> findTransactions(
            LocalDate dateFrom, LocalDate dateTo, String description, BigDecimal minAmount, BigDecimal maxAmount,
            TransactionCategory category, String source, Long customerId, Pageable pageable
            );

    TransactionResponseDto update(Long id, TransactionRequestDto dto);

    void delete(Long id);

}
