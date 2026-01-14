package ru.yandex.practicum.client.order;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient extends OrderOperations {
}
