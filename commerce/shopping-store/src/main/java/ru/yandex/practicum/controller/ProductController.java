package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.shopping_store.ShoppingStoreClient;
import ru.yandex.practicum.client.shopping_store.ShoppingStoreOperations;
import ru.yandex.practicum.dto.product.ProductCategory;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.dto.product.QuantityState;
import ru.yandex.practicum.dto.product.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ProductController implements ShoppingStoreOperations {
    private final ProductService productService;

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, @PageableDefault(size = 20) Pageable pageable) {
        return productService.getProducts(category, pageable);
    }

    @PutMapping
    public ProductDto createProduct(@Valid @RequestBody ProductDto dto) {
        return productService.createProduct(dto);
    }

    @PostMapping
    public ProductDto updateProduct(@RequestBody ProductDto dto) {
        return productService.updateProduct(dto);
    }

    @PostMapping("/removeProductFromStore")
    public boolean deleteProduct(@RequestBody UUID id) {
        return productService.deleteProduct(id);
    }

    //Это правильный метод по спецификации
//    @PostMapping("/quantityState")
//    public boolean updateStateProduct(@Valid @RequestBody SetProductQuantityStateRequest dto) {
//        return productService.updateStateProduct(dto);
//    }

    @PostMapping("/quantityState")
    public boolean updateStateProduct(@RequestParam UUID productId, @RequestParam QuantityState quantityState) {
        return productService.updateStateProduct(productId, quantityState);
    }

    @GetMapping("/{productId}")
    public ProductDto getOneProduct(@PathVariable UUID productId) {
        return productService.getOneProduct(productId);
    }

    @PostMapping("/products")
    public List<ProductDto> getProductsByIds(@RequestBody List<UUID> ids) {
        return productService.getProductsByIds(ids);
    }
}
