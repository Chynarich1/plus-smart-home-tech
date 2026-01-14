package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.model.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);

    Product toProduct(ProductDto dto);

    List<ProductDto> toDtos(List<Product> allById);
}
