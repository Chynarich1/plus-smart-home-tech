package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.delivery.DeliveryOperations;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryOperations {
    private final DeliveryService deliveryService;

    @PutMapping
    @Override
    public DeliveryDto createDelivery(@Valid @RequestBody DeliveryDto deliveryDto) {
        return deliveryService.createDelivery(deliveryDto);
    }

    @PostMapping("/successful")
    public void successfulDelivery(@NotNull @RequestBody UUID id) {
        deliveryService.successfulDelivery(id);
    }

    @PostMapping("/picked")
    public void pickedDelivery(@NotNull @RequestBody UUID id) {
        deliveryService.pickedDelivery(id);
    }

    @PostMapping("/failed")
    public void failedDelivery(@NotNull @RequestBody UUID id) {
        deliveryService.failedDelivery(id);
    }

    @PostMapping("/cost")
    @Override
    public BigDecimal calculateCostDelivery(@Valid @RequestBody OrderDto orderDto) {
        return deliveryService.calculateCostDelivery(orderDto);
    }

}
