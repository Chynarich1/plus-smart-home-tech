package ru.yandex.practicum.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.order.OrderClient;
import ru.yandex.practicum.client.shopping_store.ShoppingStoreClient;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;
import ru.yandex.practicum.dto.payment.PaymentStatus;
import ru.yandex.practicum.dto.product.ProductDto;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.PaymentMapper;
import ru.yandex.practicum.model.Payment;
import ru.yandex.practicum.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final ShoppingStoreClient shoppingStoreClient;
    private final OrderClient orderClient;

    @Transactional
    public PaymentDto createPayment(@Valid OrderDto orderDto) {
        if (orderDto.getDeliveryPrice() == null)
            throw new NotEnoughInfoInOrderToCalculateException("Не данных о стоимости доставки");

        Payment newPayment = Payment.builder()
                .totalPayment(calculateTotalCost(orderDto))
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(calculateFee(orderDto))
                .orderId(orderDto.getOrderId())
                .build();
        return paymentMapper.toDto(paymentRepository.save(newPayment));
    }

    public BigDecimal calculateTotalCost(@Valid OrderDto orderDto) {
        return calculateCostWithFee(orderDto).add(orderDto.getDeliveryPrice());
    }

    private BigDecimal calculateFee(OrderDto orderDto) {
        return calculateProductCost(orderDto).multiply(BigDecimal.valueOf(0.1));
    }

    private BigDecimal calculateCostWithFee(OrderDto orderDto) {
        BigDecimal productCost = calculateProductCost(orderDto);
        BigDecimal fee = calculateFee(orderDto);
        return productCost.add(fee);
    }

    private Payment getById(UUID id) {
        return paymentRepository.findById(id).orElseThrow(() ->
                new NotEnoughInfoInOrderToCalculateException("Не найдена оплата"));
    }

    @Transactional
    public void refundPayment(@NotNull UUID id) {
        Payment payment = getById(id);
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        orderClient.payOrder(payment.getOrderId());
    }

    public BigDecimal calculateProductCost(@Valid OrderDto orderDto) {
        Set<UUID> productIds = orderDto.getProducts().keySet();

        List<ProductDto> productDtos =
                shoppingStoreClient.getProductsByIds(new ArrayList<>(productIds));

        return productDtos.stream()
                .map(product -> product.getPrice()
                        .multiply(BigDecimal.valueOf(
                                orderDto.getProducts().get(product.getProductId())
                        )))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    public void failedPayment(@NotNull UUID id) {
        Payment payment = getById(id);
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        orderClient.failOrder(payment.getOrderId());
    }
}
