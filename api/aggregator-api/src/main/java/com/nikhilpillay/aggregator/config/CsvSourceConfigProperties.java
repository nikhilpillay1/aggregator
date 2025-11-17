package com.nikhilpillay.aggregator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

@Data
@Configuration
@ConfigurationProperties(prefix = "csv-source")
public class CsvSourceConfigProperties {

    private Map<String, CsvConfig> configs;

    public Set<String> getAvailableSources() {
        return configs.keySet();
    }

    public CsvConfig getConfig(String source) {
        return configs.get(source);
    }

    public boolean isValidSource(String source) {
        return configs.containsKey(source);
    }

    @Data
    public static class CsvConfig {
        private String headerLine;
        private String dateKey;
        private String amountKey;
        private String descriptionKey;
        private String dateFormat;

        public String[] getHeaders() {
            String[] headers = headerLine.replaceAll("\"", "").split(",");
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headers[i].trim();
            }
            return headers;
        }
    }
}
