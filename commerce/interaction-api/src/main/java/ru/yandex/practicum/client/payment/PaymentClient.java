package ru.yandex.practicum.client.payment;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.client.FeignClientConfiguration;

@FeignClient(name = "payment", path = "/api/v1/payment", configuration = FeignClientConfiguration.class)
public interface PaymentClient extends PaymentOperations {
}
