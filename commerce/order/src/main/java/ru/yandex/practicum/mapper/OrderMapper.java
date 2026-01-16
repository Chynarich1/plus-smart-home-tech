package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.model.Order;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order toModel(OrderDto dto, String username);

    OrderDto toDto(Order order);

    List<OrderDto> toDtos(List<Order> orders);
}
