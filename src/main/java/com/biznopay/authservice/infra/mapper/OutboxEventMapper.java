package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.infra.outbox.OutboxEvent;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;

public class OutboxEventMapper {
    private OutboxEventMapper() {
    }
    public static OutboxEventJpaEntity toJpaEntity(OutboxEvent event) {
        return new OutboxEventJpaEntity(
                event.getId(),
                event.getAggregateId(),
                event.getEventType(),
                event.getSubject(),
                event.getPayload(),
                event.getStatus(),
                event.getRetryCount(),
                event.getLastError(),
                event.getCreatedAt(),
                event.getPublishedAt()
        );
    }

    public static OutboxEvent toDomain(OutboxEventJpaEntity entity) {
        return OutboxEvent.reconstitute(
                entity.getId(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getSubject(),
                entity.getPayload(),
                entity.getStatus(),
                entity.getRetryCount(),
                entity.getLastError(),
                entity.getCreatedAt(),
                entity.getPublishedAt()
        );
    }
}