package ru.yandex.practicum.client.warehouse;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "warehouse", path = "/api/v1/warehouse", fallbackFactory = WarehouseFallbackFactory.class)
public interface WarehouseClient extends WarehouseOperations {
}
