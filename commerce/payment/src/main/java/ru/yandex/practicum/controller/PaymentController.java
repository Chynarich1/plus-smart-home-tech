package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.client.payment.PaymentOperations;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {
    private final PaymentService paymentService;


    @PostMapping
    @Override
    public PaymentDto createPayment(@Valid @RequestBody OrderDto orderDto) {
        return paymentService.createPayment(orderDto);
    }

    @PostMapping("/totalCost")
    @Override
    public BigDecimal calculateTotalCost(@Valid @RequestBody OrderDto orderDto) {
        return paymentService.calculateTotalCost(orderDto);
    }

    //Эм, на сколько я знаю refund это возврат средств
    @PostMapping("/refund")
    @Override
    public void refundPayment(@NotNull @RequestBody UUID id) {
        paymentService.refundPayment(id);
    }

    @PostMapping("/productCost")
    @Override
    public BigDecimal calculateProductCost(@Valid @RequestBody OrderDto orderDto) {
        return paymentService.calculateProductCost(orderDto);
    }

    @PostMapping("/failed")
    @Override
    public void failedPayment(@NotNull @RequestBody UUID id) {
        paymentService.failedPayment(id);
    }

}
