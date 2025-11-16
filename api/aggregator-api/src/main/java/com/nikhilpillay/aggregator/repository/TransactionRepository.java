package com.nikhilpillay.aggregator.repository;

import com.nikhilpillay.aggregator.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
