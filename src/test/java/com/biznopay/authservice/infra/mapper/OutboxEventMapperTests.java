package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.infra.outbox.OutboxEvent;
import com.biznopay.authservice.infra.outbox.OutboxStatus;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag("unit")
public class OutboxEventMapperTests {
    @Test
    @DisplayName("Should map to JPA entity")
    public void shouldMapToJpaEntity() {
        OutboxEvent event = OutboxEvent.create(UUID.randomUUID(), "enventType", "subject", "payload");
        OutboxEventJpaEntity entity = OutboxEventMapper.toJpaEntity(event);

        Assertions.assertEquals(event.getId(), entity.getId());
        Assertions.assertEquals(event.getAggregateId(), entity.getAggregateId());
        Assertions.assertEquals(event.getEventType(), entity.getEventType());
        Assertions.assertEquals(event.getSubject(), entity.getSubject());
        Assertions.assertEquals(event.getPayload(), entity.getPayload());
        Assertions.assertEquals(event.getCreatedAt(), entity.getCreatedAt());
        Assertions.assertEquals(event.getStatus(), entity.getStatus());
        Assertions.assertEquals(event.getRetryCount(), entity.getRetryCount());
        Assertions.assertEquals(event.getLastError(), entity.getLastError());
        Assertions.assertEquals(event.getLastError(), entity.getLastError());
        Assertions.assertEquals(event.getPublishedAt(), entity.getPublishedAt());
    }

    @Test
    @DisplayName("Should map to domain entity")
    public void shouldMapToDomainEntity() {
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity(UUID.randomUUID(), UUID.randomUUID(), "eventType",
                "subject", "payload", OutboxStatus.PENDING, 0, null, LocalDateTime.now(), null);

        OutboxEvent event = OutboxEventMapper.toDomain(entity);

        Assertions.assertEquals(entity.getId(), event.getId());
        Assertions.assertEquals(entity.getAggregateId(), entity.getAggregateId());
        Assertions.assertEquals(entity.getEventType(), entity.getEventType());
        Assertions.assertEquals(entity.getSubject(), entity.getSubject());
        Assertions.assertEquals(entity.getPayload(), entity.getPayload());
        Assertions.assertEquals(entity.getStatus(), entity.getStatus());
        Assertions.assertEquals(entity.getRetryCount(), entity.getRetryCount());
        Assertions.assertEquals(entity.getLastError(), entity.getLastError());
        Assertions.assertEquals(entity.getCreatedAt(), entity.getCreatedAt());
        Assertions.assertEquals(entity.getPublishedAt(), entity.getPublishedAt());
    }
}
