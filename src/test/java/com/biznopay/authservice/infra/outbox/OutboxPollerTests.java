package com.biznopay.authservice.infra.outbox;

import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.OutboxEventJpaRepository;
import io.nats.client.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Collections;

import static com.biznopay.authservice.infra.outbox.OutboxPoller.LOCK_KEY;
import static com.biznopay.authservice.infra.outbox.OutboxPoller.LOCK_TTL_MS;
import static com.biznopay.authservice.testcases.OutboxEventTestCases.failedOutboxJpaEntity;
import static com.biznopay.authservice.testcases.OutboxEventTestCases.validOutboxJpaEntity;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class OutboxPollerTests {

    @InjectMocks
    private OutboxPoller outboxPoller;

    @Mock
    private OutboxEventJpaRepository repository;

    @Mock
    private Connection natsConnection;

    @Mock
    StringRedisTemplate redisTemplate;

    @Mock
    ValueOperations<String, String> valueOperations;

    @BeforeEach
    public void setUp() {
        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Should publish event successfully")
    void shouldPublishEventSuccessfully() throws Exception {
        OutboxEventJpaEntity entity = validOutboxJpaEntity();
        Mockito.when(repository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.singletonList(entity));
        Mockito.when(valueOperations.setIfAbsent(LOCK_KEY,"locked", Duration.ofMillis(LOCK_TTL_MS) )).thenReturn(true);
        outboxPoller.poll();
        Mockito.verify(natsConnection).publish(Mockito.anyString(), Mockito.any());
        Mockito.verify(repository).save(Mockito.any());
    }

    @Test
    @DisplayName("Should register failure when nats publish fails")
    void shouldRegisterFailureWhenNatsPublishFails() throws Exception {
        OutboxEventJpaEntity entity = validOutboxJpaEntity();
        Mockito.when(repository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.singletonList(entity));
        Mockito.doThrow(new RuntimeException("Connection refused")).when(natsConnection).publish(Mockito.anyString(), Mockito.any());
        Mockito.when(valueOperations.setIfAbsent(LOCK_KEY,"locked", Duration.ofMillis(LOCK_TTL_MS) )).thenReturn(true);
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
        OutboxEventJpaEntity entity = failedOutboxJpaEntity();
        ;
        Mockito.when(repository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.singletonList(entity));
        Mockito.doThrow(new RuntimeException("Connection refused")).when(natsConnection).publish(Mockito.anyString(), Mockito.any());
        Mockito.when(valueOperations.setIfAbsent(LOCK_KEY,"locked", Duration.ofMillis(LOCK_TTL_MS) )).thenReturn(true);
        outboxPoller.poll();
        Mockito.verify(repository).save(Mockito.argThat(saved -> saved.getStatus() == OutboxStatus.FAILED
        ));
    }

    @Test
    @DisplayName("Should do nothing when no pending events")
    void shouldDoNothingWhenNoPendingEvents() throws Exception {
        Mockito.when(repository.findByStatus(OutboxStatus.PENDING)).thenReturn(Collections.emptyList());
        Mockito.when(valueOperations.setIfAbsent(LOCK_KEY,"locked", Duration.ofMillis(LOCK_TTL_MS) )).thenReturn(true);
        outboxPoller.poll();
        Mockito.verify(natsConnection, Mockito.never()).publish(Mockito.anyString(), Mockito.any());
    }
}
