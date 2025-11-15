package com.nikhilpillay.aggregator.mapper;

import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionResponseMapper {

    Transaction toEntity(TransactionResponseDto dto);

    TransactionResponseDto toDto(Transaction entity);

}
