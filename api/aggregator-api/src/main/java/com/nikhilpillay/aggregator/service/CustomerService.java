package com.nikhilpillay.aggregator.service;

import com.nikhilpillay.aggregator.model.dto.CustomerRequestDto;
import com.nikhilpillay.aggregator.model.dto.CustomerResponseDto;

import java.util.List;

public interface CustomerService {

    CustomerResponseDto create(CustomerRequestDto dto);

    CustomerResponseDto findById(Long id);

    List<CustomerResponseDto> findAll();

    CustomerResponseDto update(Long id, CustomerRequestDto dto);

    void delete(Long id);
}
