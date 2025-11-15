package com.nikhilpillay.aggregator.mapper;

import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.dto.TransactionRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionRequestMapper {

    Transaction toEntity(TransactionRequestDto dto);

    TransactionRequestDto toDto(Transaction entity);

}
