package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.*;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.model.Booking;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.Stock;
import ru.yandex.practicum.repository.BookingRepository;
import ru.yandex.practicum.repository.ProductRepository;
import ru.yandex.practicum.repository.StockRepository;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private static final String[] ADDRESSES =
            new String[]{"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final WarehouseMapper warehouseMapper;
    private final BookingRepository bookingRepository;

    private boolean isExistsProduct(UUID id) {
        return productRepository.existsById(id);
    }

    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest dto) {
        if (isExistsProduct(dto.getProductId())) throw new SpecifiedProductAlreadyInWarehouseException(
                "Товар с таким описанием уже зарегистрирован на складе");

        Product newProduct = warehouseMapper.toProduct(dto);

        newProduct = productRepository.save(newProduct);

        Stock newStock = Stock.builder()
                .product(newProduct)
                .quantity(0L)
                .build();

        stockRepository.save(newStock);
    }

    private List<Stock> getAllProductById(Map<UUID, Long> products) {
        List<Stock> stocks = stockRepository.findAllByProductIdIn(products.keySet());

        if (stocks.size() < products.size())
            throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе");

        return stocks;
    }

    public BookedProductsDto checkProductsNumber(ShoppingCartDto dto) {
        Map<UUID, Long> products = dto.getProducts();

        List<Stock> stocks = getAllProductById(products);

        BookedProductsDto bookedProductsDto = new BookedProductsDto();

        for (Stock stock : stocks) {
            Long requestedQuantity = products.get(stock.getProductId());
            if (requestedQuantity > stock.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе");
            }
            bookedProductsDto.setDeliveryVolume(
                    bookedProductsDto.getDeliveryVolume()
                            + getVolumeProduct(stock.getProduct()) * requestedQuantity
            );
            bookedProductsDto.setDeliveryWeight(
                    bookedProductsDto.getDeliveryWeight() +
                            stock.getProduct().getWeight() * requestedQuantity
            );
            if (stock.getProduct().getFragile()) bookedProductsDto.setFragile(true);
        }

        return bookedProductsDto;
    }

    private Double getVolumeProduct(Product product) {
        return product.getDepth() * product.getHeight() * product.getWidth();
    }


    public void takeProducts(AddProductToWarehouseRequest dto) {
        Stock stock = stockRepository.findById(dto.getProductId()).orElseThrow(() ->
                new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе"));

        stock.setQuantity(stock.getQuantity() + dto.getQuantity());

        stockRepository.save(stock);
    }

    public AddressDto getAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Transactional
    public void createTransfer(@Valid ShippedToDeliveryRequest request) {
        Booking booking = bookingRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NoOrderFoundException("Заказ не был собран на складе"));

        booking.setDeliveryId(request.getDeliveryId());

        bookingRepository.save(booking);
    }

    @Transactional
    public void returnProducts(@NotNull Map<UUID, Long> products) {
        List<Stock> stocks = stockRepository.findAllByProductIdIn(products.keySet());

        stocks.forEach(stock -> {
            Long add = products.get(stock.getProductId());
            if (add != null) {
                stock.setQuantity(stock.getQuantity() + add);
            }
        });

        Set<UUID> existingIds = stocks.stream()
                .map(Stock::getProductId)
                .collect(Collectors.toSet());

        products.forEach((productId, qty) -> {
            if (!existingIds.contains(productId)) {
                stocks.add(Stock.builder().productId(productId).quantity(qty).build());
            }
        });

        stockRepository.saveAll(stocks);
    }

    @Transactional
    public BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request) {
        Map<UUID, Long> requestedProducts = request.getProducts();

        List<Stock> stocks = stockRepository.findAllByProductIdIn(requestedProducts.keySet());

        List<Product> productsInfo = productRepository.findAllById(requestedProducts.keySet());

        double totalWeight = 0;
        double totalVolume = 0;
        boolean fragile = false;

        for (Stock stock : stocks) {
            Long requestedQuantity = requestedProducts.get(stock.getProductId());
            if (requestedQuantity > stock.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse("Недостаточно товара на складе");
            }

            stock.setQuantity(stock.getQuantity() - requestedQuantity);
            Product p = productsInfo.stream().filter(prod -> prod.getProductId().equals(stock.getProductId()))
                    .findFirst().orElseThrow();

            totalWeight += p.getWeight() * requestedQuantity;
            totalVolume += getVolumeProduct(p) * requestedQuantity;

            if (p.getFragile()) fragile = true;
        }

        stockRepository.saveAll(stocks);

        Booking booking = Booking.builder()
                .orderId(request.getOrderId())
                .products(requestedProducts)
                .build();
        bookingRepository.save(booking);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(fragile)
                .build();
    }
}
