package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.dto.delivery.DeliveryState;

import java.util.UUID;

@Entity
@Table(name = "deliveries", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID deliveryId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "from_address_id")
    private Address fromAddress;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "to_address_id")
    private Address toAddress;
    @Column(nullable = false)
    private UUID orderId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryState deliveryState;
}
