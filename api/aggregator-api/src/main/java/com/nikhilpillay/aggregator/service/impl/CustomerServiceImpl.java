package com.nikhilpillay.aggregator.service.impl;

import com.nikhilpillay.aggregator.mapper.CustomerRequestMapper;
import com.nikhilpillay.aggregator.mapper.CustomerResponseMapper;
import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.model.dto.CustomerRequestDto;
import com.nikhilpillay.aggregator.model.dto.CustomerResponseDto;
import com.nikhilpillay.aggregator.repository.CustomerRepository;
import com.nikhilpillay.aggregator.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    private final CustomerRequestMapper requestMapper;

    private final CustomerResponseMapper responseMapper;

    @Override
    public CustomerResponseDto create(CustomerRequestDto dto) {
        Customer customer = requestMapper.toEntity(dto);
        Customer saved = repository.save(customer);
        return responseMapper.toDto(saved);
    }

    @Override
    public List<CustomerResponseDto> findAll() {
        return repository.findAll().stream().map(responseMapper::toDto).toList();
    }

    @Override
    public CustomerResponseDto findById(Long id) {
        return repository.findById(id).map(responseMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public CustomerResponseDto update(Long id, CustomerRequestDto dto) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customer.setName(dto.name());

        Customer updated = repository.save(customer);
        return responseMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
