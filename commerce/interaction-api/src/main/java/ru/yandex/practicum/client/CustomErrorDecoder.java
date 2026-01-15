package ru.yandex.practicum.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import ru.yandex.practicum.exception.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = null;
        String rawBody = "";

        try {
            if (response.body() != null) {
                try (InputStream bodyIs = response.body().asInputStream()) {
                    rawBody = StreamUtils.copyToString(bodyIs, StandardCharsets.UTF_8);
                    log.error("Ошибка тела {}: {}", methodKey, rawBody);
                }
            }
        } catch (IOException e) {
            return new RuntimeException("Ошибка чтения " + e.getMessage());
        }

        if (!rawBody.isEmpty()) {
            try {
                ErrorResponse errorResponse = objectMapper.readValue(rawBody, ErrorResponse.class);
                message = errorResponse.getUserMessage();
                if (message == null) {
                    message = errorResponse.getMessage();
                }
            } catch (Exception e) {
                message = rawBody;
            }
        }

        if (message == null) {
            message = "Непонятная ошибка код " + response.status();
        }

        if (response.status() == 400) {
            if (message.contains("не находится в требуемом количестве")) {
                return new ProductInShoppingCartLowQuantityInWarehouse(message);
            }
            if (message.contains("Айди совпадает")) {
                return new SpecifiedProductAlreadyInWarehouseException(message);
            }
            if (message.contains("Нет информации о товаре")) {
                return new NoSpecifiedProductInWarehouseException(message);
            }
            if (message.contains("Нет искомых товаров")) {
                return new NoProductsInShoppingCartException(message);
            }
            if (message.contains("Заказ не найден")) {
                return new NoOrderFoundException(message);
            }
            if (message.contains("Недостаточно информации")) {
                return new NotEnoughInfoInOrderToCalculateException(message);
            }
            return new RuntimeException(message);
        }

        if (response.status() == 404) {
            if (message.contains("доставка")) {
                return new NoDeliveryFoundException(message);
            }
            if (message.contains("Товар не найден")) {
                return new ProductNotFoundException(message);
            }
            return new ProductNotFoundException(message);
        }

        if (response.status() == 401) {
            return new NotAuthorizedUserException(message);
        }

        if (response.status() == 503) {
            return new ServiceUnavailableException("Сервис временно недоступен: " + message);
        }

        return new Default().decode(methodKey, response);
    }
}
