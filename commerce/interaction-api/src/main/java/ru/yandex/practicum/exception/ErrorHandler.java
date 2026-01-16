package ru.yandex.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(final ProductNotFoundException e) {
        return ErrorResponseFabric.responseFabric(e, "Товар не найден", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorizedUser(final NotAuthorizedUserException e) {
        return ErrorResponseFabric.responseFabric(e, "Неавторизованный пользователь", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    public ResponseEntity<ErrorResponse> handleNoProductsInShoppingCart(final NoProductsInShoppingCartException e) {
        return ErrorResponseFabric.responseFabric(e, "Нет искомых товаров в корзине", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SpecifiedProductAlreadyInWarehouseException.class)
    public ResponseEntity<ErrorResponse>
    handleSpecifiedProductAlreadyInWarehouse(final SpecifiedProductAlreadyInWarehouseException e) {
        return ErrorResponseFabric.responseFabric(e, "Айди совпадает с имеющимся на складе", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProductInShoppingCartLowQuantityInWarehouse.class)
    public ResponseEntity<ErrorResponse>
    handleProductInShoppingCartLowQuantityInWarehouse(final ProductInShoppingCartLowQuantityInWarehouse e) {
        return ErrorResponseFabric.responseFabric
                (e, "Товар из корзины не находится в требуемом количестве на складе", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSpecifiedProductInWarehouseException.class)
    public ResponseEntity<ErrorResponse>
    handleNoSpecifiedProductInWarehouse(final NoSpecifiedProductInWarehouseException e) {
        return ErrorResponseFabric.responseFabric
                (e, "Нет информации о товаре на складе", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> handleServiceUnavailable(final ServiceUnavailableException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Склад отдыхает", "message", e.getMessage()));
    }

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<ErrorResponse>
    handleNoOrderFoundException(final NoOrderFoundException e) {
        return ErrorResponseFabric.responseFabric(e, "Заказ не найден", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotEnoughInfoInOrderToCalculateException.class)
    public ResponseEntity<ErrorResponse>
    handleNotEnoughInfoInOrderToCalculateException(final NotEnoughInfoInOrderToCalculateException e) {
        return ErrorResponseFabric.responseFabric(e, "Недостаточно информации для калькуляции ордера",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoDeliveryFoundException.class)
    public ResponseEntity<ErrorResponse>
    handleNoDeliveryFoundException(final NoDeliveryFoundException e) {
        return ErrorResponseFabric.responseFabric(e, "Не найдена доставка", HttpStatus.NOT_FOUND);
    }
}
