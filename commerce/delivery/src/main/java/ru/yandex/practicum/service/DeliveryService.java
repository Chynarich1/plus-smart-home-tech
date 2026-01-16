package ru.yandex.practicum.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.order.OrderClient;
import ru.yandex.practicum.client.warehouse.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.warehouse.ShippedToDeliveryRequest;
import ru.yandex.practicum.exception.NoDeliveryFoundException;
import ru.yandex.practicum.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.mapper.DeliveryMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Delivery;
import ru.yandex.practicum.repository.DeliveryRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;
    private final BigDecimal BASE_COST;

    private final String DOUBLE_ADDRESS_NAME;

    public DeliveryService(
            DeliveryRepository deliveryRepository,
            DeliveryMapper deliveryMapper,
            OrderClient orderClient,
            WarehouseClient warehouseClient,
            @Value("${delivery.service.base.cost}") BigDecimal baseCost,
            @Value("${delivery.service.double.address.name}") String doubleAddressName
    ) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryMapper = deliveryMapper;
        this.orderClient = orderClient;
        this.warehouseClient = warehouseClient;
        this.BASE_COST = baseCost;
        this.DOUBLE_ADDRESS_NAME = doubleAddressName;
    }

    @Transactional
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryMapper.toModel(deliveryDto);

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return deliveryMapper.toDto(savedDelivery);
    }

    private Delivery getById(UUID id) {
        return deliveryRepository.findById(id).orElseThrow(() ->
                new NoDeliveryFoundException("Не найдена доставка"));
    }

    private Delivery changeStatus(UUID id, DeliveryState state) {
        Delivery existingDelivery = getById(id);
        existingDelivery.setDeliveryState(state);
        return deliveryRepository.save(existingDelivery);
    }

    @Transactional
    public void successfulDelivery(UUID id) {
        Delivery existingDelivery = changeStatus(id, DeliveryState.DELIVERED);

        orderClient.deliverOrder(existingDelivery.getOrderId());
    }

    @Transactional
    public void pickedDelivery(UUID id) {
        Delivery existingDelivery = changeStatus(id, DeliveryState.IN_PROGRESS);

        orderClient.assemblyOrder(existingDelivery.getOrderId());

        warehouseClient.createTransfer(ShippedToDeliveryRequest.builder()
                .orderId(existingDelivery.getOrderId())
                .deliveryId(existingDelivery.getDeliveryId())
                .build());
    }

    @Transactional
    public void failedDelivery(UUID id) {
        Delivery existingDelivery = changeStatus(id, DeliveryState.FAILED);

        orderClient.deliverFailedOrder(existingDelivery.getOrderId());
    }

    private boolean isDoubleAddress(Address address) {
        return address.getFullAddress().contains(DOUBLE_ADDRESS_NAME);
    }

    private boolean isOneStreet(Delivery delivery) {
        return delivery.getFromAddress().getStreet().equals(delivery.getToAddress().getStreet());
    }

    @Transactional
    public BigDecimal calculateCostDelivery(OrderDto orderDto) {
        if (orderDto.getDeliveryId() == null) throw new NotEnoughInfoInOrderToCalculateException("Недостаточно данных");
        BigDecimal outCost = BASE_COST;
        Delivery orderDelivery = getById(orderDto.getDeliveryId());

        orderDelivery.setDeliveryWeight(orderDto.getDeliveryWeight());
        orderDelivery.setDeliveryVolume(orderDto.getDeliveryVolume());
        orderDelivery.setFragile(orderDto.getFragile());
        deliveryRepository.save(orderDelivery);

        if (isDoubleAddress(orderDelivery.getFromAddress())) {
            outCost = outCost.multiply(BigDecimal.valueOf(2)).add(BASE_COST);
        } else {
            outCost = outCost.add(BASE_COST);
        }

        if (Boolean.TRUE.equals(orderDto.getFragile())) {
            BigDecimal fragileCost = outCost.multiply(BigDecimal.valueOf(0.2));
            outCost = outCost.add(fragileCost);
        }

        outCost = outCost
                .add(BigDecimal.valueOf(orderDto.getDeliveryWeight())
                        .multiply(BigDecimal.valueOf(0.3)));

        outCost = outCost
                .add(BigDecimal.valueOf(orderDto.getDeliveryVolume())
                        .multiply(BigDecimal.valueOf(0.2)));

        if (!isOneStreet(orderDelivery)) {
            outCost = outCost
                    .add(outCost.multiply(BigDecimal.valueOf(0.2)));
        }

        return outCost;
    }
}
