package ru.yandex.practicum.client.payment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentOperations {
    @PostMapping
    PaymentDto createPayment(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCost(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/refund")
    void refundPayment(@NotNull @RequestBody UUID id);

    @PostMapping("/productCost")
    BigDecimal calculateProductCost(@Valid @RequestBody OrderDto orderDto);

    @PostMapping("/failed")
    void failedPayment(@NotNull @RequestBody UUID id);
}
