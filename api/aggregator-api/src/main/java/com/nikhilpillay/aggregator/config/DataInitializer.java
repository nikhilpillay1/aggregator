package com.nikhilpillay.aggregator.config;

import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(CustomerRepository customerRepository) {
        return args -> {
            if (customerRepository.count() == 0) {
                Customer customer = new Customer();
                customer.setName("Default Customer");
                customerRepository.save(customer);
            }
        };
    }
}
