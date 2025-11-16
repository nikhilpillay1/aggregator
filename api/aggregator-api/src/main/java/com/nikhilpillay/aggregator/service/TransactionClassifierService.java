package com.nikhilpillay.aggregator.service;

import com.nikhilpillay.aggregator.model.enums.TransactionCategory;

public interface TransactionClassifierService {

    TransactionCategory classify(String description);

}
