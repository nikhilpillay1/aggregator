package com.nikhilpillay.aggregator.controller;

import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.service.impl.CustomerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/customers")
public class CustomerController {

    private final CustomerServiceImpl customerService;

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }
}
