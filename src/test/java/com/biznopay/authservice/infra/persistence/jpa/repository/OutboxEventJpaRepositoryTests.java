package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.config.PostgresContainerBase;
import com.biznopay.authservice.infra.outbox.OutboxStatus;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Tag("unit")
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OutboxEventJpaRepositoryTests extends PostgresContainerBase {

    @Autowired
    private OutboxEventJpaRepository outboxEventJpaRepository;

    @BeforeEach
    void setup() {
        outboxEventJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return empty list if there is no OutboxEvent")
    public void shouldReturnEmptyListIfThereIsNoOutboxEvent() {
        List<OutboxEventJpaEntity> result = outboxEventJpaRepository.findAll();
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return List of OutboxEvent on findByStatus")
    public void shouldReturnListOfOutboxEventOnFindByStatus() {
        UUID outboxEventId = UUID.randomUUID();
        UUID aggregateId = UUID.randomUUID();
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity(outboxEventId, aggregateId, "event_type",
                "subject", "payload", OutboxStatus.PENDING, 0, null, LocalDateTime.now(), LocalDateTime.now());
        outboxEventJpaRepository.save(entity);
        List<OutboxEventJpaEntity> result = outboxEventJpaRepository.findByStatus(OutboxStatus.PENDING);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(entity.getId(), result.getFirst().getId());
        Assertions.assertEquals(entity.getAggregateId(), result.getFirst().getAggregateId());
        Assertions.assertEquals(entity.getEventType(), result.getFirst().getEventType());
        Assertions.assertEquals(entity.getEventType(), result.getFirst().getEventType());
        Assertions.assertEquals(entity.getEventType(), result.getFirst().getEventType());
        Assertions.assertEquals(entity.getSubject(), result.getFirst().getSubject());
        Assertions.assertEquals(entity.getStatus(), result.getFirst().getStatus());
        Assertions.assertEquals(entity.getRetryCount(), result.getFirst().getRetryCount());
        Assertions.assertEquals(entity.getLastError(), result.getFirst().getLastError());
        Assertions.assertEquals(entity.getCreatedAt(), result.getFirst().getCreatedAt());
        Assertions.assertEquals(entity.getPublishedAt(), result.getFirst().getPublishedAt());
    }
}

