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

    @Test
    @DisplayName("Should throw RequiredFieldException if payload is null or empty")
    public void shouldThrowRequiredFieldExceptionIfPayloadIsNullOrEmptyOnCreate() {
        Assertions.assertThrows(RequiredFieldException.class, () ->
                OutboxEvent.create(UUID.randomUUID(), "eventType", "subject", null));
    }

    @Test
    @DisplayName("Should create OutboxEvent with correct values")
    public void shouldCreateOutboxEventWithCorrectValues(){
        OutboxEvent event =  OutboxEvent.create(UUID.randomUUID(), "eventType", "subject", "payload");

        Assertions.assertNotNull(event.getId());
        Assertions.assertEquals("eventType", event.getEventType());
        Assertions.assertEquals("subject", event.getSubject());
        Assertions.assertEquals("payload", event.getPayload());
        Assertions.assertEquals(OutboxStatus.PENDING, event.getStatus());
        Assertions.assertEquals(0, event.getRetryCount());
        Assertions.assertNull(event.getLastError());
        Assertions.assertNotNull(event.getCreatedAt());
        Assertions.assertNull(event.getPublishedAt());
    }

    @Test
    @DisplayName("Should mark OutboxEvent as published")
    public void shouldMarkOutboxEventAsPublished(){
        OutboxEvent event =  OutboxEvent.create(UUID.randomUUID(), "eventType", "subject", "payload");
        event.markPublished();
        Assertions.assertEquals(OutboxStatus.PUBLISHED, event.getStatus());
        Assertions.assertNotNull(event.getPublishedAt());
    }

    @Test
    @DisplayName("Should register OutboxEvent as failed")
    public void shouldRegisterFailure(){
        OutboxEvent event =  OutboxEvent.create(UUID.randomUUID(), "eventType", "subject", "payload");
        event.registerFailure("error");
        Assertions.assertEquals(1, event.getRetryCount());
        Assertions.assertEquals("error", event.getLastError());
        Assertions.assertEquals(OutboxStatus.PENDING, event.getStatus());
    }
}
