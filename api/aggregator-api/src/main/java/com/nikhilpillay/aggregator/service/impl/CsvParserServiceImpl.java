package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.config.CsvSourceConfigProperties;
import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
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

    @Override
    public List<Transaction> parseCsv(MultipartFile file, Long customerId, String source) throws IOException {

        CsvSourceConfigProperties.CsvConfig config = csvSourceConfigProperties.getConfigs().get(source);
        if (config == null) {
            throw new IllegalArgumentException("No configuration found for: " + source);
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer ID"));

        List<Transaction> transactions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) { //skip lines until the header line is found
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

    private Transaction parseRecord(CSVRecord record, Customer customer, String source, CsvSourceConfigProperties.CsvConfig config) {
        Transaction transaction = new Transaction();

        //parse date
        String dateStr = record.get(config.getDateKey());
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(config.getDateFormat()));
        transaction.setDate(date);

        //this is quite messy because I didn't expect multiple relevant keys for a column, but one data source had this
        //parse amount
        if (config.getAmountKey().contains(",")) {
            String[] amountKeys = config.getAmountKey().split(",");

            double total = 0;
            for (String amountKey : amountKeys) {
                if (!record.get(amountKey).isEmpty()) {
                    total += Double.parseDouble((record.get(amountKey)));
                }
            }
            transaction.setAmount(BigDecimal.valueOf(total));
        } else {
            String amountStr = record.get(config.getAmountKey()).replace(",", "");
            BigDecimal amount = new BigDecimal(amountStr);
            transaction.setAmount(amount);
        }

        //parse description
        if (config.getDescriptionKey().contains(",")) {
            String[] descriptionKeys = config.getDescriptionKey().split(",");

            StringBuilder stringBuilder = new StringBuilder();
            for (String descriptionKey : descriptionKeys) {
                stringBuilder.append(" ").append(record.get(descriptionKey));
            }
            transaction.setDescription(stringBuilder.toString());
        } else {
            String description = record.get(config.getDescriptionKey());
            transaction.setDescription(description);
        }

        //classify transaction category
        try {
            TransactionCategory category = transactionClassifierService.classify(transaction.getDescription());
            transaction.setCategory(category);
        } catch (Exception e) {
            transaction.setCategory(TransactionCategory.OTHER);
        }

        transaction.setSource(source);

        transaction.setCustomer(customer);
        return transaction;
    }

}
