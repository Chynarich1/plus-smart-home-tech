package ru.yandex.practicum.client.payment;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient extends PaymentOperations {
}
