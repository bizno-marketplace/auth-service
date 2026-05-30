package com.biznopay.authservice.infra.persistence.jpa.entity;

import com.biznopay.authservice.domain.enums.UserStatus;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "T_SUPER_ADMINS")
@DiscriminatorValue("SUPER_ADMIN")
public class SuperAdminJpaEntity extends UserJpaEntity {
    public SuperAdminJpaEntity(UUID id, String firstName, String lastName, String email, String phone, String password,
                               UserStatus status, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
    }

    public SuperAdminJpaEntity() {

    }
}
