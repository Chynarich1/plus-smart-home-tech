package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.product.*;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products = productRepository.getProductsByProductCategory(category,
                pageable);
        return products.map(productMapper::toDto);
    }

    public ProductDto createProduct(ProductDto dto) {
        Product product = productRepository.save(productMapper.toProduct(dto));
        return productMapper.toDto(product);
    }

    private Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("Продукт не найден"));
    }

    public ProductDto updateProduct(ProductDto dto) {
        Product existingProduct = findById(dto.getProductId());

        if (dto.getProductName() != null) {
            existingProduct.setProductName(dto.getProductName());
        }

        if (dto.getDescription() != null) {
            existingProduct.setDescription(dto.getDescription());
        }

        if (dto.getImageSrc() != null) {
            existingProduct.setImageSrc(dto.getImageSrc());
        }

        if (dto.getQuantityState() != null) {
            existingProduct.setQuantityState(dto.getQuantityState());
        }

        if (dto.getProductState() != null) {
            existingProduct.setProductState(dto.getProductState());
        }

        if (dto.getProductCategory() != null) {
            existingProduct.setProductCategory(dto.getProductCategory());
        }

        if (dto.getPrice() != null) {
            existingProduct.setPrice(dto.getPrice());
        }

        return productMapper.toDto(productRepository.save(existingProduct));
    }

    public boolean deleteProduct(UUID id) {
        Product existingProduct = findById(id);

        existingProduct.setProductState(ProductState.DEACTIVATE);

        productRepository.save(existingProduct);
        return true;
    }

    public boolean updateStateProduct(SetProductQuantityStateRequest dto) {
        Product existingProduct = findById(dto.getProductId());

        existingProduct.setQuantityState(dto.getQuantityState());

        productRepository.save(existingProduct);
        return true;
    }

    public boolean updateStateProduct(UUID productId, QuantityState quantityState) {
        return updateStateProduct(SetProductQuantityStateRequest.builder()
                .productId(productId)
                .quantityState(quantityState)
                .build());
    }

    public ProductDto getOneProduct(UUID id) {
        Product existingProduct = findById(id);

        return productMapper.toDto(existingProduct);
    }

    public List<ProductDto> getProductsByIds(List<UUID> ids) {
        return productMapper.toDtos(productRepository.findAllById(ids));
    }
}
