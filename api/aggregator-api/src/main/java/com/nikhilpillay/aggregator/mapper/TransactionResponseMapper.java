package com.nikhilpillay.aggregator.mapper;

import com.nikhilpillay.aggregator.model.Transaction;
import com.nikhilpillay.aggregator.model.dto.TransactionResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionResponseMapper {

    Transaction toEntity(TransactionResponseDto dto);

    TransactionResponseDto toDto(Transaction entity);

    List<TransactionResponseDto> toDtoList(List<Transaction> entities);

}
