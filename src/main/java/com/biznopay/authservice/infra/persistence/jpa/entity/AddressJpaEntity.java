package com.biznopay.authservice.infra.persistence.jpa.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AddressJpaEntity {
    private Double latitude;
    private Double longitude;
    private String street;
    private String neighbourhood;
    private String city;
    private String province;
    private String country;
}