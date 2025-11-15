package com.nikhilpillay.aggregator.mapper;

import com.nikhilpillay.aggregator.model.Customer;
import com.nikhilpillay.aggregator.model.dto.CustomerRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerRequestMapper {

    Customer toEntity(CustomerRequestDto dto);

    CustomerRequestDto toDto(Customer entity);

}
