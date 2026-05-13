package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Tag("unit")
@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActivationTokenJpaRepositoryTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:latest")
    );

    @Autowired
    private ActivationTokenJpaRepository activationTokenJpaRepository;

    @BeforeEach
    public void setUp() {
        activationTokenJpaRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save ActivationTokenJpaEntity with correct values")
    public void shouldSaveActivationTokenWithCorrectValues() {
        ActivationTokenJpaEntity entity = new ActivationTokenJpaEntity(UUID.randomUUID(),UUID.randomUUID(),false, LocalDateTime.now(),LocalDateTime.now());
        activationTokenJpaRepository.save(entity);
        Optional<ActivationTokenJpaEntity> result = activationTokenJpaRepository.findById(entity.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(entity.getId(), result.get().getId());
        Assertions.assertEquals(entity.getUserId(), result.get().getUserId());
        Assertions.assertEquals(entity.isUsed(), result.get().isUsed());
        Assertions.assertEquals(entity.getExpiresAt(), result.get().getExpiresAt());
        Assertions.assertEquals(entity.getCreatedAt(), result.get().getCreatedAt());
    }
}
