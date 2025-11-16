package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.mapper.TransactionRequestMapper;
import com.nikhilpillay.aggregator.mapper.TransactionResponseMapper;
import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.model.enums.TransactionSource;
import com.nikhilpillay.aggregator.repository.CustomerRepository;
import com.nikhilpillay.aggregator.repository.TransactionRepository;
import com.nikhilpillay.aggregator.service.CsvParserService;
import com.nikhilpillay.aggregator.service.TransactionClassifierService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FnbCsvParserServiceImpl implements CsvParserService {

    private final TransactionClassifierService transactionClassifierService;

    private final CustomerRepository customerRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public List<Transaction> parseCsv(MultipartFile file, Long customerId) throws IOException {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) { //skip lines until you reach the header
                if (line.trim().startsWith("Date, Amount, Balance, Description")) {
                    break;
                }
            }

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withHeader("Date", "Amount", "Balance", "Description")
                    .withSkipHeaderRecord(false)
                    .withTrim();

            try (CSVParser csvParser = new CSVParser(reader, csvFormat)) {
                for (CSVRecord record : csvParser) {
                        Transaction transaction = parseRecord(record, customer);
                        transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    private Transaction parseRecord(CSVRecord record,Customer customer) {
        Transaction transaction = new Transaction();

        //parse date
        String dateStr = record.get("Date");
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
        transaction.setDate(date);

        //parse amount
        String amountStr = record.get("Amount").replace(",", "");
        BigDecimal amount = new BigDecimal(amountStr);
        transaction.setAmount(amount);

        //parse description
        String description = record.get("Description");
        transaction.setDescription(description);

        //classify transaction category
        try {
        TransactionCategory category = transactionClassifierService.classify(description);
        transaction.setCategory(category);
        } catch (Exception e) {
            transaction.setCategory(TransactionCategory.OTHER);
        }

        transaction.setSource(TransactionSource.FNB);

        transaction.setCustomer(customer);
        return transaction;
    }

}
