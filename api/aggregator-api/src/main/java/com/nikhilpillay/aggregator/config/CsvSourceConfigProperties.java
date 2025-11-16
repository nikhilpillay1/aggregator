package com.nikhilpillay.aggregator.config;

import com.nikhilpillay.aggregator.model.enums.TransactionSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "csv-source")
public class CsvSourceConfigProperties {

    private Map<TransactionSource, CsvConfig> configs;

    @Data
    public static class CsvConfig {
        private String headerLine;
        private String dateKey;
        private String amountKey;
        private String descriptionKey;

        public String[] getHeaders() {
            String[] headers = headerLine.replaceAll("\"", "").split(",");
            for (int i = 0; i < headers.length; i++) { //fixes an issue where spaces in the header-line can't be mapped
                headers[i] = headers[i].trim();
            }
            return headers;
        }
    }
}
