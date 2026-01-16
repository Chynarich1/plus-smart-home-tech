package ru.yandex.practicum.client.warehouse;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;

public interface WarehouseOperations {
    @PostMapping("/check")
    BookedProductsDto checkProductsNumber(@Valid @RequestBody ShoppingCartDto dto);

    @PostMapping("/assembly")
    BookedProductsDto assemblyProducts(@RequestBody AssemblyProductsForOrderRequest request);

    @GetMapping("/address")
    AddressDto getAddress();

    @PostMapping("/shipped")
    void createTransfer(@Valid @RequestBody ShippedToDeliveryRequest request);
}
