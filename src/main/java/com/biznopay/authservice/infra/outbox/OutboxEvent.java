package com.biznopay.authservice.infra.outbox;

import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;
import java.util.UUID;

public class OutboxEvent {

    public static final int MAX_RETRIES = 3;

    private final UUID id;
    private final UUID aggregateId;
    private final String eventType;
    private final String subject;
    private final String payload;
    private final LocalDateTime createdAt;
    private OutboxStatus status;
    private int retryCount;
    private String lastError;
    private LocalDateTime publishedAt;

    private OutboxEvent(UUID id, UUID aggregateId, String eventType, String subject, String payload, OutboxStatus status,
                        int retryCount, String lastError, LocalDateTime createdAt, LocalDateTime publishedAt) {
        this.id = id;
        this.aggregateId = this.validateAggregateId(aggregateId);
        this.eventType = this.validateEventType(eventType);
        this.subject = this.validateSubject(subject);
        this.payload = payload;
        this.status = status;
        this.retryCount = retryCount;
        this.lastError = lastError;
        this.createdAt = createdAt;
        this.publishedAt = publishedAt;
    }

    public static OutboxEvent create(UUID aggregateId, String eventType, String subject, String payload) {
        return new OutboxEvent(UUID.randomUUID(), aggregateId, eventType, subject, payload, OutboxStatus.PENDING,
                0, null, LocalDateTime.now(), null);
    }

    public static OutboxEvent reconstitute(UUID id, UUID aggregateId, String eventType, String subject, String payload,
                                           OutboxStatus status, int retryCount, String lastError, LocalDateTime createdAt,
                                           LocalDateTime publishedAt) {
        return new OutboxEvent(id, aggregateId, eventType, subject, payload, status, retryCount, lastError, createdAt, publishedAt);
    }

    private UUID validateAggregateId(UUID aggregateId) {
        if (aggregateId == null) throw new RequiredFieldException("aggregateId", OutboxEvent.class.getName(),"OUTBOX_EVENT-001");
        return aggregateId;
    }

    private String validateEventType(String eventType) {
        if (eventType == null || eventType.trim().isEmpty()) throw new RequiredFieldException("eventType", OutboxEvent.class.getName(),"OUTBOX_EVENT-002");
        return eventType;
    }

    private String validateSubject(String subject) {
        if (subject == null || subject.trim().isEmpty()) throw new RequiredFieldException("subject", OutboxEvent.class.getName(),"OUTBOX_EVENT-003");
        return subject;
    }

    public void markPublished() {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    public void registerFailure(String error) {
        this.retryCount++;
        this.lastError = error;
        if (this.retryCount >= MAX_RETRIES) this.status = OutboxStatus.FAILED;
    }

    public boolean hasExhaustedRetries() {
        return this.retryCount >= MAX_RETRIES;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getSubject() {
        return subject;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public String getLastError() {
        return lastError;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
}