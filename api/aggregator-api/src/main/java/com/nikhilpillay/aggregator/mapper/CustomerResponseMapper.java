package com.nikhilpillay.aggregator.mapper;

import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.model.dto.CustomerResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerResponseMapper {

    Customer toEntity(CustomerResponseDto dto);

    CustomerResponseDto toDto(Customer entity);

}
