package ru.yandex.practicum.client.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.client.FeignClientConfiguration;

@FeignClient(name = "delivery", path = "/api/v1/delivery", configuration = FeignClientConfiguration.class)
public interface DeliveryClient extends DeliveryOperations{
}
