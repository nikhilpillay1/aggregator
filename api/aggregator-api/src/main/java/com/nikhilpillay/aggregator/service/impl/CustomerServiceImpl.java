package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.repository.CustomerRepository;
import com.nikhilpillay.aggregator.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Customer findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public Customer update(Long id, Customer customer) {
        Customer existing = findById(id);
        existing.setFirstName(customer.getFirstName());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
