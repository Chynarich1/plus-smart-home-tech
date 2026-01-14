package ru.yandex.practicum.client.order;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;

import java.util.UUID;

public interface OrderOperations {
    @PostMapping("/payment")
    OrderDto payOrder(UUID id);

    @PostMapping("/payment/failed")
    OrderDto failOrder(@NotNull @RequestBody UUID id);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(UUID id);

    @PostMapping("/delivery")
    OrderDto deliverOrder(@NotNull @RequestBody UUID id);

    @PostMapping("/delivery/failed")
    OrderDto deliverFailedOrder(@NotNull @RequestBody UUID id);
}
