package com.biznopay.authservice.testcases;

import com.biznopay.authservice.infra.outbox.OutboxStatus;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class OutboxEventTestCases {
    public static OutboxEventJpaEntity validOutboxJpaEntity() {
        return new OutboxEventJpaEntity(UUID.randomUUID(), UUID.randomUUID(), "USER", "SUBJECT", "PAYLOAD", OutboxStatus.PENDING, 0, "ERROR", LocalDateTime.now(), LocalDateTime.now());
    }

    public static OutboxEventJpaEntity failedOutboxJpaEntity() {
        return new OutboxEventJpaEntity(UUID.randomUUID(), UUID.randomUUID(), "USER", "SUBJECT", "PAYLOAD", OutboxStatus.FAILED, 0, "ERROR", LocalDateTime.now(), LocalDateTime.now());
    }
}
