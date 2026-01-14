package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.warehouse.WarehouseClient;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.cart.ShoppingCartState;
import ru.yandex.practicum.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.exception.NotAuthorizedUserException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {
    private final ShoppingCartRepository repository;
    private final ShoppingCartMapper mapper;

    private final WarehouseClient warehouseClient;

    private ShoppingCart getOrInitActiveCart(String username) {
        validateUser(username);
        return repository.findByUsernameAndState(username, ShoppingCartState.ACTIVE)
                .orElseGet(() -> {
                    ShoppingCart newCart = ShoppingCart.builder()
                            .username(username)
                            .state(ShoppingCartState.ACTIVE)
                            .products(new HashMap<>())
                            .build();
                    return repository.save(newCart);
                });
    }

    //Я не очень понял как через джакарту в контроллере выбрасывать кастомное исключение поэтому пусть будет тут
    private void validateUser(String username) {
        if (username == null || username.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }

    private ShoppingCart getActiveCartOrThrow(String username) {
        validateUser(username);
        return repository.findByUsernameAndState(username, ShoppingCartState.ACTIVE)
                .orElseThrow(() -> new NoProductsInShoppingCartException("У пользователя нет активной корзины"));
    }

    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart shoppingCart = getOrInitActiveCart(username);

        return mapper.toDto(shoppingCart);
    }

    public ShoppingCartDto addToShoppingCart(String username, Map<UUID, Long> products) {
        ShoppingCart shoppingCart = getOrInitActiveCart(username);
        products.forEach((k, v) -> shoppingCart.getProducts().merge(k, v, Long::sum));
        ShoppingCartDto cartDtoToCheck = mapper.toDto(shoppingCart);

        warehouseClient.checkProductsNumber(cartDtoToCheck);


        return mapper.toDto(repository.save(shoppingCart));
    }

    public void deactivateCurrentShoppingCart(String username) {
        ShoppingCart shoppingCart = getActiveCartOrThrow(username);
        shoppingCart.setState(ShoppingCartState.DEACTIVATE);
        repository.save(shoppingCart);
    }

    public ShoppingCartDto removeFromShoppingCart(String username, List<UUID> productIds) {
        ShoppingCart shoppingCart = getActiveCartOrThrow(username);
        Map<UUID, Long> products = shoppingCart.getProducts();

        for (UUID id : productIds) {
            if (!products.containsKey(id)) {
                throw new NoProductsInShoppingCartException("Не все продукты найдены в корзине");
            } else {
                products.remove(id);
            }
        }

        shoppingCart.setProducts(products);

        return mapper.toDto(repository.save(shoppingCart));
    }

    public ShoppingCartDto changeProductQuantity(String username, ChangeProductQuantityRequestDto request) {
        ShoppingCart shoppingCart = getActiveCartOrThrow(username);

        shoppingCart.getProducts().put(request.getProductId(), request.getNewQuantity());

        return mapper.toDto(repository.save(shoppingCart));
    }
}
