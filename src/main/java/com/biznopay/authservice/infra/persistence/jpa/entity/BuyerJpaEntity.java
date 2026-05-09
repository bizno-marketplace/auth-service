package com.biznopay.authservice.infra.persistence.jpa.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BUYER")
public class BuyerJpaEntity extends UserJpaEntity {
}
