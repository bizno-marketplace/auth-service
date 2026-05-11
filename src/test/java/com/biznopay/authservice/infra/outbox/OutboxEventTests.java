package com.biznopay.authservice.infra.outbox;

import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class OutboxEventTests {
    @Test
    @DisplayName("Should throw RequiredFieldException if aggregateId is null on create")
    public void shouldThrowRequiredFieldExceptionIfAggregateIdIsNullOnCreate() {
        Assertions.assertThrows(RequiredFieldException
                .class, () -> OutboxEvent.create(null, "eventType", "subject", "payload"));
    }
}
