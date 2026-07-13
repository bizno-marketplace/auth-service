package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.infra.outbox.OutboxStatus;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
    List<OutboxEventJpaEntity> findByStatus(OutboxStatus status);

    Optional<OutboxEventJpaEntity> findByAggregateId(UUID aggregateId);
}