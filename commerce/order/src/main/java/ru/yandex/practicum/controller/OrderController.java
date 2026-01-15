package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.order.OrderOperations;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.product.ProductReturnRequest;
import ru.yandex.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderOperations {
    private final OrderService orderService;

    @GetMapping
    public List<OrderDto> getUserOrders(@NotNull @RequestParam String username) {
        return orderService.getUserOrders(username);
    }

    //По спецификации здесь нет юзернейма
    @PutMapping
    public OrderDto createNewOrder(@Valid @RequestBody CreateNewOrderRequest newOrder,
                                   @NotNull @RequestParam String username) {
        return orderService.createNewOrder(newOrder, username);
    }

    //По спецификации здесь еще должен быть дублирующий объект в параметре, но это полная бредятина, я не буду это делать
    @PostMapping("/return")
    public OrderDto returnOrder(@Valid @RequestBody ProductReturnRequest returnRequest) {
        return orderService.returnOrder(returnRequest);
    }

    @Override
    public OrderDto payOrder(UUID id) {
        return orderService.payOrder(id);
    }

    @Override
    public OrderDto failOrder(UUID id) {
        return orderService.failOrder(id);
    }

    @Override
    public OrderDto deliverOrder(UUID id) {
        return orderService.deliverOrder(id);
    }

    @Override
    public OrderDto deliverFailedOrder(UUID id) {
        return orderService.deliverFailedOrder(id);
    }

    @PostMapping("/completed")
    public OrderDto completeOrder(@NotNull @RequestBody UUID id) {
        return orderService.completeOrder(id);
    }

    @PostMapping("/calculate/total")
    public OrderDto calculateTotalPrice(@NotNull @RequestBody UUID id) {
        return orderService.calculateTotalPrice(id);
    }

    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryPrice(@NotNull @RequestBody UUID id) {
        return orderService.calculateDeliveryPrice(id);
    }

    @Override
    public OrderDto assemblyOrder(UUID id) {
        return orderService.assemblyOrder(id);
    }

    @PostMapping("/assembly/failed")
    public OrderDto assemblyFailOrder(@NotNull @RequestBody UUID id) {
        return orderService.assemblyFailOrder(id);
    }


}
