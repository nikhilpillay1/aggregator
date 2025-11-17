package com.nikhilpillay.aggregator.service;

import com.nikhilpillay.aggregator.model.dto.TransactionRequestDto;
import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import com.nikhilpillay.aggregator.model.enums.TransactionSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TransactionService {

    List<TransactionResponseDto> importTransactionsFromCsv(MultipartFile file, Long customerId, TransactionSource source) throws IOException;

    TransactionResponseDto findById(Long id);

    List<TransactionResponseDto> findAll();

    TransactionResponseDto update(Long id, TransactionRequestDto dto);

    void delete(Long id);

}
