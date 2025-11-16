package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.config.CsvSourceConfigProperties;
import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.model.enums.TransactionSource;
import com.nikhilpillay.aggregator.repository.CustomerRepository;
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
public class CsvParserServiceImpl implements CsvParserService {

    private final TransactionClassifierService transactionClassifierService;

    private final CustomerRepository customerRepository;

    private final CsvSourceConfigProperties csvSourceConfigProperties;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Override
    public List<Transaction> parseCsv(MultipartFile file, Long customerId, TransactionSource source) throws IOException {

        CsvSourceConfigProperties.CsvConfig config = csvSourceConfigProperties.getConfigs().get(source);
        if (config == null) {
            throw new IllegalArgumentException("No configuration found for: " + source);
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) { //skip lines until you reach the header
                if (line.contains(config.getHeaderLine())) {
                    break;
                }
            }

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withHeader(config.getHeaders())
                    .withSkipHeaderRecord(false)
                    .withTrim();

            try (CSVParser csvParser = new CSVParser(reader, csvFormat)) {
                for (CSVRecord record : csvParser) {
                    Transaction transaction = parseRecord(record, customer, source, config);
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }

    private Transaction parseRecord(CSVRecord record, Customer customer, TransactionSource source, CsvSourceConfigProperties.CsvConfig config) {
        Transaction transaction = new Transaction();

        //parse date
        String dateStr = record.get(config.getDateKey());
        LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
        transaction.setDate(date);

        //parse amount
        String amountStr = record.get(config.getAmountKey()).replace(",", "");
        BigDecimal amount = new BigDecimal(amountStr);
        transaction.setAmount(amount);

        //parse description
        String description = record.get(config.getDescriptionKey());
        transaction.setDescription(description);

        //classify transaction category
        try {
            TransactionCategory category = transactionClassifierService.classify(description);
            transaction.setCategory(category);
        } catch (Exception e) {
            transaction.setCategory(TransactionCategory.OTHER);
        }

        transaction.setSource(source);

        transaction.setCustomer(customer);
        return transaction;
    }

}
