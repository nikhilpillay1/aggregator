package com.nikhilpillay.aggregator.repository;

import com.nikhilpillay.aggregator.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
