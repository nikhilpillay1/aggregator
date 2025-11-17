package com.nikhilpillay.aggregator.controller;

import com.nikhilpillay.aggregator.model.dto.CustomerRequestDto;
import com.nikhilpillay.aggregator.model.dto.CustomerResponseDto;
import com.nikhilpillay.aggregator.service.impl.CustomerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/customers")
public class CustomerController {

    private final CustomerServiceImpl service;

    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@Valid @RequestBody CustomerRequestDto dto) {
        CustomerResponseDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDto>> findAll() {
        List<CustomerResponseDto> customers = service.findAll();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> findById(@PathVariable Long id) {
        CustomerResponseDto customer = service.findById(id);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDto dto) {

        CustomerResponseDto updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
