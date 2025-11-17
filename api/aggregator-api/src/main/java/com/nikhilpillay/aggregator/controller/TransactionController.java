package com.nikhilpillay.aggregator.controller;

import com.nikhilpillay.aggregator.config.CsvSourceConfigProperties;
import com.nikhilpillay.aggregator.model.dto.TransactionRequestDto;
import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import com.nikhilpillay.aggregator.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    private final CsvSourceConfigProperties configProperties;

    @PostMapping("/upload")
    public ResponseEntity<List<TransactionResponseDto>> uploadTransactions(
            @RequestParam("file") MultipartFile file,
            @RequestParam("customerId") Long customerId,
            @RequestParam("source") String source) throws IOException {
            List<TransactionResponseDto> transactions = service.importTransactionsFromCsv(file, customerId, source);
            return ResponseEntity.ok(transactions);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDto>> findAll() {
        List<TransactionResponseDto> transactions = service.findAll();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> findById(@PathVariable Long id) {
        TransactionResponseDto transaction = service.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDto dto) {

        TransactionResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sources")
    public Set<String> getAvailableSources() {
        return configProperties.getAvailableSources();
    }
}
