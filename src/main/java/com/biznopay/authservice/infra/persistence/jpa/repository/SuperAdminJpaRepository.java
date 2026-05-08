package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SuperAdminJpaRepository extends JpaRepository<SuperAdminJpaEntity, UUID> {
    long countBy();
}
