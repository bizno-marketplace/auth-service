package com.biznopay.authservice.infra.persistence.jpa.entity;

import com.biznopay.authservice.domain.enums.UserStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@DiscriminatorValue("BUYER")
public class BuyerJpaEntity extends UserJpaEntity {
    @Embedded
    private AddressJpaEntity deliveryAddress;

    public BuyerJpaEntity(UUID id, String firstName, String lastName, String email, String phone, String password,
                          UserStatus status, AddressJpaEntity deliveryAddress, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
        this.deliveryAddress = deliveryAddress;
    }

    public BuyerJpaEntity() {

    }
}
