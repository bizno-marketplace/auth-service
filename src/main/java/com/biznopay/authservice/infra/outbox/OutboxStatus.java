package com.biznopay.authservice.infra.outbox;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}