package com.nikhilpillay.aggregator.service;

import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TransactionService {

    List<TransactionResponseDto> importTransactionsFromCsv(MultipartFile file, Long customerId) throws IOException;

}
