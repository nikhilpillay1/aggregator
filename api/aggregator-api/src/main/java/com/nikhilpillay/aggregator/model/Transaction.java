package com.nikhilpillay.aggregator.model;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import com.nikhilpillay.aggregator.model.enums.TransactionSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate date;

    private String description;

    private BigDecimal amount;

    private TransactionSource source;

    private TransactionCategory category;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
