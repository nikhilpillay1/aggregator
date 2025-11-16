package com.nikhilpillay.aggregator.service;

import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.enums.TransactionSource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CsvParserService {

    public List<Transaction> parseCsv(MultipartFile file, Long customerId, TransactionSource source) throws IOException;

}
