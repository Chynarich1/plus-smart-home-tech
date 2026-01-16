package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Table(name = "addresses", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    private UUID addressId;
    private String country;
    private String city;
    private String street;
    private String house;
    private String flat;

    public String getFullAddress() {
        return getCountry() +
                ", " +
                getCity() +
                ", " +
                getStreet() +
                ", " +
                getHouse() +
                ", " +
                getFlat();
    }
}
