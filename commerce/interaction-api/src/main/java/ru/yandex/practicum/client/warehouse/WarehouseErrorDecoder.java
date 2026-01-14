package ru.yandex.practicum.client.warehouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.exception.ErrorResponse;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.ProductNotFoundException;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class WarehouseErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String s, Response response) {
        String message = "Неизвестная ошибка";

        try (InputStream bodyIs = response.body().asInputStream()) {

            ErrorResponse errorResponse = objectMapper.readValue(bodyIs, ErrorResponse.class);
            message = errorResponse.getUserMessage();


            if (response.status() == 400) {
                //Не знаю как отличить по другому, не добавляя новые поля в класс ошибки, а добавлять нельзя потому
                //что будет нарушена спецификация
                if (message.contains("Товар из корзины не находится в требуемом количестве на складе")) {
                    return new ProductInShoppingCartLowQuantityInWarehouse(message);
                }

                return new RuntimeException(message);
            }

            if (response.status() == 404) {
                return new ProductNotFoundException(message);
            }

        } catch (IOException e) {
            return new RuntimeException("Ошибка чтения ответа от склада");
        }

        return new Default().decode(s, response);
    }
}
