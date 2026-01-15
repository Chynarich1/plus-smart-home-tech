package ru.yandex.practicum.client.warehouse;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.client.FeignClientConfiguration;


@FeignClient(name = "warehouse", path = "/api/v1/warehouse", configuration = FeignClientConfiguration.class)
public interface WarehouseClient extends WarehouseOperations {
}
