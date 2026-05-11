package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.infra.outbox.OutboxStatus;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.TestcontainersConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OutboxEventJpaRepositoryTests {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:latest")
    );

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
                "subject", "payload", OutboxStatus.PENDING, 0, null, LocalDateTime.now(),LocalDateTime.now());
        outboxEventJpaRepository.save(entity);
        List<OutboxEventJpaEntity> result = outboxEventJpaRepository.findByStatus(OutboxStatus.PENDING);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(entity.getId(), result.getFirst().getId());
        Assertions.assertEquals(entity.getAggregateId(), result.getFirst().getAggregateId());
        Assertions.assertEquals(entity.getEventType(),result.getFirst().getEventType());
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

