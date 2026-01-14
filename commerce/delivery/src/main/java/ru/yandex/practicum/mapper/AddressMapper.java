package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.model.Address;


@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "addressId", ignore = true)
    Address toModel(AddressDto dto);

    AddressDto toDto(Address model);
}
