package com.biznopay.authservice.infra.outbox;

import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class OutboxEventTests {
    @Test
    @DisplayName("Should throw RequiredFieldException if aggregateId is null on create")
    public void shouldThrowRequiredFieldExceptionIfAggregateIdIsNullOnCreate() {
        Assertions.assertThrows(RequiredFieldException.class, () ->
                OutboxEvent.create(null, "eventType", "subject", "payload"));
    }

    @Test
    @DisplayName("Should RequiredFieldException if eventType is null or empty")
    public void shouldThrowRequiredFieldExceptionIfEventTypeIsNullOrEmptyOnCreate() {
        Assertions.assertThrows(RequiredFieldException.class, () ->
                OutboxEvent.create(UUID.randomUUID(), null, "subject", "payload"));
    }

    @Test
    @DisplayName("Should RequiredFieldException if subject is null or empty")
    public void shouldThrowRequiredFieldExceptionIfSubjectIsNullOrEmptyOnCreate() {
        Assertions.assertThrows(RequiredFieldException.class, () ->
                OutboxEvent.create(UUID.randomUUID(), "eventType", null, "payload"));
    }
}
