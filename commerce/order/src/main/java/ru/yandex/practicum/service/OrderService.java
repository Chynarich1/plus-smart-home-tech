package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.client.delivery.DeliveryClient;
import ru.yandex.practicum.client.payment.PaymentClient;
import ru.yandex.practicum.client.warehouse.WarehouseClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.DeliveryState;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.OrderState;
import ru.yandex.practicum.dto.product.ProductReturnRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.exception.NoOrderFoundException;
import ru.yandex.practicum.mapper.OrderMapper;
import ru.yandex.practicum.model.Address;
import ru.yandex.practicum.model.Order;
import ru.yandex.practicum.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final DeliveryClient deliveryClient;
    private final PaymentClient paymentClient;
    private final OrderMapper orderMapper;
    private final WarehouseClient warehouseClient;

    public List<OrderDto> getUserOrders(String username) {
        List<Order> orders = orderRepository.findByUsername(username);
        return orderMapper.toDtos(orders);
    }

    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest newOrderRequest, String username) {
        Order newOrder = Order.builder()
                .shoppingCartId(newOrderRequest.getShoppingCart().getShoppingCartId())
                .products(newOrderRequest.getShoppingCart().getProducts())
                .address(Address.builder()
                        .country(newOrderRequest.getDeliveryAddress().getCountry())
                        .city(newOrderRequest.getDeliveryAddress().getCity())
                        .street(newOrderRequest.getDeliveryAddress().getStreet())
                        .house(newOrderRequest.getDeliveryAddress().getHouse())
                        .flat(newOrderRequest.getDeliveryAddress().getFlat())
                        .build())
                .username(username)
                .state(OrderState.NEW)
                .build();

        newOrder = orderRepository.save(newOrder);

        BookedProductsDto bookedInfo = warehouseClient.assemblyProducts(
                AssemblyProductsForOrderRequest.builder()
                        .orderId(newOrder.getOrderId())
                        .products(newOrder.getProducts())
                        .build()
        );

        newOrder.setDeliveryWeight(bookedInfo.getDeliveryWeight());
        newOrder.setDeliveryVolume(bookedInfo.getDeliveryVolume());
        newOrder.setFragile(bookedInfo.getFragile());

        AddressDto warehouseAddress = warehouseClient.getAddress();

        DeliveryDto deliveryRequest = DeliveryDto.builder()
                .orderId(newOrder.getOrderId())
                .fromAddress(warehouseAddress)
                .toAddress(newOrderRequest.getDeliveryAddress())
                .deliveryState(DeliveryState.CREATED)
                .build();

        DeliveryDto plannedDelivery = deliveryClient.createDelivery(deliveryRequest);

        newOrder.setDeliveryId(plannedDelivery.getDeliveryId());

        return orderMapper.toDto(orderRepository.save(newOrder));
    }

    private Order findOrderById(UUID id) {
        return orderRepository.findByOrderId(id).orElseThrow(() ->
                new NoOrderFoundException("Нету заказа такого"));
    }

    private OrderDto changeStatus(UUID id, OrderState state) {
        Order order = findOrderById(id);
        order.setState(state);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto returnOrder(ProductReturnRequest returnRequest) {
        return changeStatus(returnRequest.getOrderId(), OrderState.PRODUCT_RETURNED);
    }

    public OrderDto payOrder(UUID id) {
        return changeStatus(id, OrderState.PAID);
    }

    public OrderDto failOrder(UUID id) {
        return changeStatus(id, OrderState.PAYMENT_FAILED);
    }

    public OrderDto deliverOrder(UUID id) {
        return changeStatus(id, OrderState.DELIVERED);
    }

    public OrderDto deliverFailedOrder(UUID id) {
        return changeStatus(id, OrderState.DELIVERY_FAILED);
    }

    public OrderDto completeOrder(UUID id) {
        return changeStatus(id, OrderState.COMPLETED);
    }

    @Transactional
    public OrderDto calculateTotalPrice(UUID id) {
        Order order = findOrderById(id);
        OrderDto orderDto = orderMapper.toDto(order);

        BigDecimal productCost = paymentClient.calculateProductCost(orderDto);
        order.setProductPrice(productCost);

        orderDto.setProductPrice(productCost);

        BigDecimal totalCost = paymentClient.calculateTotalCost(orderDto);
        order.setTotalPrice(totalCost);

        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto calculateDeliveryPrice(UUID id) {
        Order order = findOrderById(id);
        order.setDeliveryPrice(deliveryClient.calculateCostDelivery(orderMapper.toDto(order)));
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto assemblyOrder(UUID id) {
        return changeStatus(id, OrderState.ASSEMBLED);
    }

    public OrderDto assemblyFailOrder(UUID id) {
        return changeStatus(id, OrderState.ASSEMBLY_FAILED);
    }
}
