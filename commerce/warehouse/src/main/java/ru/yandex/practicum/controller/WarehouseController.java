package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.warehouse.WarehouseOperations;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperations {
    private final WarehouseService warehouseService;

    @PutMapping
    public void addNewProduct(@Valid @RequestBody NewProductInWarehouseRequest dto) {
        warehouseService.addNewProduct(dto);
    }

    @Override
    public BookedProductsDto checkProductsNumber(@Valid @RequestBody ShoppingCartDto dto) {
        return warehouseService.checkProductsNumber(dto);
    }

    @PostMapping("/add")
    public void takeProducts(@Valid @RequestBody AddProductToWarehouseRequest dto) {
        warehouseService.takeProducts(dto);
    }


    @GetMapping("/address")
    public AddressDto getAddress() {
        return warehouseService.getAddress();
    }

    @PostMapping("/shipped")
    public void createTransfer(@Valid @RequestBody ShippedToDeliveryRequest request) {
        warehouseService.createTransfer(request);
    }

    @PostMapping("/return")
    public void returnProducts(@NotNull @RequestBody Map<UUID, Long> products) {
        warehouseService.returnProducts(products);
    }

    @PostMapping("/assembly")
    public BookedProductsDto assemblyProducts(@Valid @RequestBody AssemblyProductsForOrderRequest request) {
        return warehouseService.assemblyProducts(request);
    }
}
