package com.nikhilpillay.aggregator.model;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
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

    @Enumerated(value = EnumType.STRING)
    private TransactionCategory category;

    private String source;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
