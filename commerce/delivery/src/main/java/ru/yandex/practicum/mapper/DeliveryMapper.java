package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.model.Delivery;


@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface DeliveryMapper {

    Delivery toModel(DeliveryDto dto);

    DeliveryDto toDto(Delivery delivery);
}
