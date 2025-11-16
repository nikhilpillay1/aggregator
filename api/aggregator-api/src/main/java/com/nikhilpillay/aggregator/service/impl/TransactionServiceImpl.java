package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.mapper.TransactionResponseMapper;
import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import com.nikhilpillay.aggregator.repository.TransactionRepository;
import com.nikhilpillay.aggregator.service.CsvParserService;
import com.nikhilpillay.aggregator.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CsvParserService csvParserService;

    private final TransactionResponseMapper responseMapper;

    private final TransactionRepository transactionRepository;

    @Override
    public List<TransactionResponseDto> importTransactionsFromCsv(MultipartFile file, Long customerId) throws IOException {
        List<Transaction> parsedTransactions = csvParserService.parseCsv(file, customerId);
        List<Transaction> savedTransactions = transactionRepository.saveAll(parsedTransactions);
        return responseMapper.toDtoList(savedTransactions);
    }
}
