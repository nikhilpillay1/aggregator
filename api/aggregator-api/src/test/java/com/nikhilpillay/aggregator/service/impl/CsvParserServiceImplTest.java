package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.config.CsvSourceConfigProperties;
import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.repository.CustomerRepository;
import com.nikhilpillay.aggregator.service.TransactionClassifierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsvParserServiceImplTest {

    @Mock
    private TransactionClassifierService transactionClassifierService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CsvSourceConfigProperties csvSourceConfigProperties;

    @InjectMocks
    private CsvParserServiceImpl csvParserService;

    private Customer testCustomer;
    private CsvSourceConfigProperties.CsvConfig testConfig;
    private Map<String, CsvSourceConfigProperties.CsvConfig> configMap;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");

        testConfig = new CsvSourceConfigProperties.CsvConfig();
        testConfig.setHeaderLine("Date,Description,Amount");
        testConfig.setDateKey("Date");
        testConfig.setDescriptionKey("Description");
        testConfig.setAmountKey("Amount");
        testConfig.setDateFormat("yyyy-MM-dd");

        configMap = new HashMap<>();
        configMap.put("test-bank", testConfig);
    }

    @Test
    void givenValidCsv_thenServiceParsesCsvAsExpected() throws IOException {
        String csvContent = "Some metadata\n" +
                "Date,Description,Amount\n" +
                "2024-01-15,Woolworths,50.00\n" +
                "2024-01-16,BP Garage Unleaded,30.50";

        MultipartFile file = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(csvSourceConfigProperties.getConfigs()).thenReturn(configMap);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionClassifierService.classify("Woolworths")).thenReturn(TransactionCategory.GROCERIES);
        when(transactionClassifierService.classify("BP Garage Unleaded")).thenReturn(TransactionCategory.TRANSPORTATION);

        List<Transaction> transactions = csvParserService.parseCsv(file, 1L, "test-bank");

        assertNotNull(transactions);
        assertEquals(2, transactions.size());

        Transaction firstTransaction = transactions.get(0);
        assertEquals(LocalDate.of(2024, 1, 15), firstTransaction.getDate());
        assertEquals("Woolworths", firstTransaction.getDescription());
        assertEquals(new BigDecimal("50.00"), firstTransaction.getAmount());
        assertEquals(TransactionCategory.GROCERIES, firstTransaction.getCategory());
        assertEquals("test-bank", firstTransaction.getSource());
        assertEquals(testCustomer, firstTransaction.getCustomer());

        Transaction secondTransaction = transactions.get(1);
        assertEquals(LocalDate.of(2024, 1, 16), secondTransaction.getDate());
        assertEquals("BP Garage Unleaded", secondTransaction.getDescription());
        assertEquals(new BigDecimal("30.50"), secondTransaction.getAmount());
        assertEquals(TransactionCategory.TRANSPORTATION, secondTransaction.getCategory());

        verify(transactionClassifierService, times(2)).classify(anyString());
    }

    @Test
    void givenVagueDescription_thenTransactionClassifiedAsOther() throws IOException {
        String csvContent = "Date,Description,Amount\n" +
                "2024-01-15,Unknown Transaction,100.00";

        MultipartFile file = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(csvSourceConfigProperties.getConfigs()).thenReturn(configMap);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionClassifierService.classify(anyString())).thenThrow(new RuntimeException("Classification failed"));

        List<Transaction> transactions = csvParserService.parseCsv(file, 1L, "test-bank");

        assertEquals(1, transactions.size());
        assertEquals(TransactionCategory.OTHER, transactions.get(0).getCategory());
    }

    @Test
    void givenUnknownSource_thenServiceThrowsException() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                "test".getBytes()
        );

        when(csvSourceConfigProperties.getConfigs()).thenReturn(configMap);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> csvParserService.parseCsv(file, 1L, "unknown-source")
        );

        assertEquals("No configuration found for: unknown-source", exception.getMessage());
    }

    @Test
    void givenEmptyFile_thenServiceReturnsNoTransactions() throws IOException {
        String csvContent = "Date,Description,Amount\n";

        MultipartFile file = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(csvSourceConfigProperties.getConfigs()).thenReturn(configMap);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        List<Transaction> transactions = csvParserService.parseCsv(file, 1L, "test-bank");

        assertNotNull(transactions);
        assertEquals(0, transactions.size());
    }

    @Test
    void givenCsvWithMultipleHeaderLines_thenUnnecessaryLinesAreSkipped() throws IOException {
        String csvContent = "Bank Statement Export\n" +
                "Account Number: 12345\n" +
                "Period: Jan 2024\n" +
                "Date,Description,Amount\n" +
                "2024-01-15,Purchase,75.25";

        MultipartFile file = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(csvSourceConfigProperties.getConfigs()).thenReturn(configMap);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionClassifierService.classify(anyString())).thenReturn(TransactionCategory.SHOPPING);

        List<Transaction> transactions = csvParserService.parseCsv(file, 1L, "test-bank");

        assertEquals(1, transactions.size());
        assertEquals("Purchase", transactions.get(0).getDescription());
    }

    @Test
    void givenCsvWithNegativeNumbers_thenServiceParsesCorrectly() throws IOException {
        String csvContent = "Date,Description,Amount\n" +
                "2024-01-15,Refund,-50.00";

        MultipartFile file = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(csvSourceConfigProperties.getConfigs()).thenReturn(configMap);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionClassifierService.classify(anyString())).thenReturn(TransactionCategory.OTHER);

        List<Transaction> transactions = csvParserService.parseCsv(file, 1L, "test-bank");

        assertEquals(1, transactions.size());
        assertEquals(new BigDecimal("-50.00"), transactions.get(0).getAmount());
    }

    @Test
    void givenDifferentDateFormat_thenServiceParsesCorrectly() throws IOException {
        testConfig.setDateFormat("dd/MM/yyyy");
        String csvContent = "Date,Description,Amount\n" +
                "15/01/2024,Test Transaction,100.00";

        MultipartFile file = new MockMultipartFile(
                "file",
                "transactions.csv",
                "text/csv",
                csvContent.getBytes()
        );

        when(csvSourceConfigProperties.getConfigs()).thenReturn(configMap);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(transactionClassifierService.classify(anyString())).thenReturn(TransactionCategory.OTHER);

        List<Transaction> transactions = csvParserService.parseCsv(file, 1L, "test-bank");

        assertEquals(1, transactions.size());
        assertEquals(LocalDate.of(2024, 1, 15), transactions.get(0).getDate());
    }
}