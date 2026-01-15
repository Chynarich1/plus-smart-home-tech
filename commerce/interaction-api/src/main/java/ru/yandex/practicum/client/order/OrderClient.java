package ru.yandex.practicum.client.order;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.client.FeignClientConfiguration;


@FeignClient(name = "order", path = "/api/v1/order", configuration = FeignClientConfiguration.class)
public interface OrderClient extends OrderOperations {
}
