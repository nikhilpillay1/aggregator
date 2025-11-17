package com.nikhilpillay.aggregator.util;

import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.enums.TransactionCategory;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecification {

    public static Specification<Transaction> withFilters(
            LocalDate dateFrom,
            LocalDate dateTo,
            String description,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            TransactionCategory category,
            String source,
            Long customerId) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Date range filter
            if (dateFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), dateFrom));
            }
            if (dateTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), dateTo));
            }

            // Description filter (case-insensitive contains)
            if (description != null && !description.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%"
                ));
            }

            // Amount range filter
            if (minAmount != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            // Category filter
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            // Source filter
            if (source != null && !source.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("source"), source));
            }

            // Customer filter (with join)
            if (customerId != null) {
                Join<Transaction, Customer> customerJoin = root.join("customer");
                predicates.add(criteriaBuilder.equal(customerJoin.get("id"), customerId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Additional convenience methods for common queries
    public static Specification<Transaction> hasCategory(TransactionCategory category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }

    public static Specification<Transaction> amountGreaterThan(BigDecimal amount) {
        return (root, query, cb) -> cb.greaterThan(root.get("amount"), amount);
    }

    public static Specification<Transaction> dateInRange(LocalDate from, LocalDate to) {
        return (root, query, cb) -> cb.between(root.get("date"), from, to);
    }

}
