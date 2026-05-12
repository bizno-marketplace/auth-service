package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ActivationTokenJpaRepository extends JpaRepository<ActivationTokenJpaEntity, UUID> {
}
