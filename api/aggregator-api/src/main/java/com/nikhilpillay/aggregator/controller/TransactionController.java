package com.nikhilpillay.aggregator.controller;

import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import com.nikhilpillay.aggregator.model.enums.TransactionSource;
import com.nikhilpillay.aggregator.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/upload")
    public ResponseEntity<List<TransactionResponseDto>> uploadTransactions(
            @RequestParam("file") MultipartFile file,
            @RequestParam("customerId") Long customerId,
            @RequestParam("source") TransactionSource source) throws IOException {
            List<TransactionResponseDto> transactions = transactionService.importTransactionsFromCsv(file, customerId, source);
            return ResponseEntity.ok(transactions);
    }
}
