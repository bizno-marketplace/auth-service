package com.biznopay.authservice.infra.persistence.jpa.entity;

import com.biznopay.authservice.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "T_BUYERS")
@DiscriminatorValue("BUYER")
public class BuyerJpaEntity extends UserJpaEntity {

    @JoinColumn(name = "fk_buyer_id")
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AddressJpaEntity> deliveryAddresses;

    public BuyerJpaEntity(UUID id, String firstName, String lastName, String email, String phone, String password,
                          UserStatus status, List<AddressJpaEntity> deliveryAddress, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
        this.deliveryAddresses = deliveryAddress;
    }

    public BuyerJpaEntity() {

    }
}
