package com.nikhilpillay.aggregator.service;

import com.nikhilpillay.aggregator.model.Customer;

import java.util.List;

public interface CustomerService {

    List<Customer> findAll();

    Customer findById(Long id);

    Customer save(Customer customer);

    Customer update(Long id, Customer customer);

    void delete(Long id);
}
