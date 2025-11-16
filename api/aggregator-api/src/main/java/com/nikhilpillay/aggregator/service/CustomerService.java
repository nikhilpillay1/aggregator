package com.nikhilpillay.aggregator.service;

import com.nikhilpillay.aggregator.model.dto.CustomerRequestDto;
import com.nikhilpillay.aggregator.model.dto.CustomerResponseDto;

import java.util.List;

public interface CustomerService {

    List<CustomerResponseDto> findAll();

    CustomerResponseDto findById(Long id);

    CustomerResponseDto create(CustomerRequestDto dto);

    CustomerResponseDto update(Long id, CustomerRequestDto dto);

    void delete(Long id);
}
