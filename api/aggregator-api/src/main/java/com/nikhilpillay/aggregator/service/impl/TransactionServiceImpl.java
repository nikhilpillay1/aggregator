package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.mapper.TransactionResponseMapper;
import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.dto.TransactionRequestDto;
import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.repository.TransactionRepository;
import com.nikhilpillay.aggregator.service.CsvParserService;
import com.nikhilpillay.aggregator.service.TransactionService;
import com.nikhilpillay.aggregator.util.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CsvParserService csvParserService;

    private final TransactionResponseMapper responseMapper;

    private final TransactionRepository repository;

    @Override
    public List<TransactionResponseDto> importTransactionsFromCsv(MultipartFile file, Long customerId, String source) throws IOException {
        List<Transaction> parsedTransactions = csvParserService.parseCsv(file, customerId, source);
        List<Transaction> savedTransactions = repository.saveAll(parsedTransactions);
        return responseMapper.toDtoList(savedTransactions);
    }

    @Override
    public List<TransactionResponseDto> findAll() {
        return repository.findAll().stream().map(responseMapper::toDto).toList();
    }

    @Override
    public TransactionResponseDto findById(Long id) {
        return repository.findById(id).map(responseMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    public Page<Transaction> findTransactions(
            LocalDate dateFrom,
            LocalDate dateTo,
            String description,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            TransactionCategory category,
            String source,
            Long customerId,
            Pageable pageable) {

        Specification<Transaction> spec = TransactionSpecification.withFilters(
                dateFrom, dateTo, description, minAmount, maxAmount, category, source, customerId
        );

        return repository.findAll(spec, pageable);
    }


    @Override
    public TransactionResponseDto update(Long id, TransactionRequestDto dto) {
        Transaction transaction = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        transaction.setDate(dto.date());
        transaction.setSource(dto.source());
        transaction.setCategory(dto.category());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());

        Transaction updated = repository.save(transaction);
        return responseMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
