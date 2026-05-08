package com.biznopay.authservice.infra.persistence.jpa.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdminJpaEntity extends UserJpaEntity {
}
