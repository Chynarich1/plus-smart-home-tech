package ru.yandex.practicum.client.shopping_store;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.product.ProductDto;

import java.util.List;
import java.util.UUID;

public interface ShoppingStoreOperations {
    @PostMapping("/products")
    List<ProductDto> getProductsByIds(@RequestBody List<UUID> ids);
}
