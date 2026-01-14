package ru.yandex.practicum.client.shopping_store;

import org.springframework.cloud.openfeign.FeignClient;


@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreClient extends ShoppingStoreOperations {
}
