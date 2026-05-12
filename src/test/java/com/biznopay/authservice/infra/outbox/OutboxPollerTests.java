package com.biznopay.authservice.infra.outbox;

import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.OutboxEventJpaRepository;
import com.biznopay.authservice.mocks.Mocks;
import io.nats.client.Connection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class OutboxPollerTests {

    @InjectMocks
    private OutboxPoller outboxPoller;

    @Mock
    private OutboxEventJpaRepository repository;

    @Mock
    private Connection natsConnection;

    @Test
    @DisplayName("Should publish event successfully")
    void shouldPublishEventSuccessfully() throws Exception {
        OutboxEventJpaEntity entity = Mocks.pendingOutboxEventJpaEntityMock();
        Mockito.when(repository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.singletonList(entity));
        outboxPoller.poll();
        Mockito.verify(natsConnection).publish(Mockito.anyString(), Mockito.any());
        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    @DisplayName("Should register failure when nats publish fails")
    void shouldRegisterFailureWhenNatsPublishFails() throws Exception {
        OutboxEventJpaEntity entity = Mocks.pendingOutboxEventJpaEntityMock();
        Mockito.when(repository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.singletonList(entity));
        Mockito.doThrow(new RuntimeException("Connection refused")).when(natsConnection).publish(Mockito.anyString(), Mockito.any());
        outboxPoller.poll();
        Mockito.verify(repository).save(Mockito.argThat(saved ->
                saved.getRetryCount() == 1 &&
                        saved.getLastError() != null &&
                        saved.getStatus() == OutboxStatus.PENDING
        ));
    }

    @Test
    @DisplayName("Should mark event as failed after exhausting retries")
    void shouldMarkEventAsFailedAfterExhaustingRetries() throws Exception {
        OutboxEventJpaEntity entity = Mocks.exhaustedOutboxEventJpaEntityMock();
        Mockito.when(repository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.singletonList(entity));
        Mockito.doThrow(new RuntimeException("Connection refused")).when(natsConnection).publish(Mockito.anyString(), Mockito.any());
        outboxPoller.poll();
        Mockito.verify(repository).save(Mockito.argThat(saved -> saved.getStatus() == OutboxStatus.FAILED
        ));
    }

    @Test
    @DisplayName("Should do nothing when no pending events")
    void shouldDoNothingWhenNoPendingEvents() throws Exception {
        Mockito.when(repository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.emptyList());
        outboxPoller.poll();
        Mockito.verify(natsConnection, Mockito.never()).publish(Mockito.anyString(), Mockito.any());
    }
}
