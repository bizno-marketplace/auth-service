package com.biznopay.authservice.infra.outbox;

import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.OutboxEventJpaRepository;
import com.biznopay.authservice.mocks.Mocks;
import io.nats.client.Connection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
public class OutboxPollerTests {
    @Mock
    private OutboxEventJpaRepository outboxEventJpaRepository;

    @Mock
    private Connection natsConnection;

    @Test
    @DisplayName("Should register failure when nats connection is not established")
    public void shouldThrowExceptionWhenNatsConnectionIsNotEstablished() {
        OutboxEventJpaEntity entity = Mocks.pendingOutboxEventJpaEntityMock();
        OutboxEventJpaEntity entity1 = Mocks.pendingOutboxEventJpaEntityMock();

        Mockito.when(outboxEventJpaRepository.findByStatus(OutboxStatus.PENDING)).thenReturn(Arrays.asList(entity, entity1));
        Mockito.doThrow(new RuntimeException("Connection refused")).when(natsConnection).publish(Mockito.anyString(), Mockito.any());

        OutboxPoller outboxPoller = new OutboxPoller(outboxEventJpaRepository, natsConnection);
        outboxPoller.poll();

        Mockito.verify(outboxEventJpaRepository, Mockito.times(2)).save(Mockito.any(OutboxEventJpaEntity.class));
        Mockito.verify(natsConnection, Mockito.times(2)).publish(Mockito.anyString(), Mockito.any());
    }
}
