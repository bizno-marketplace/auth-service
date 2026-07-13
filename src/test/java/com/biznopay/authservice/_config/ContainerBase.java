package com.biznopay.authservice._config;

import io.github.amadeusitgroup.testcontainers.nats.NatsContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class ContainerBase {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bizno_auth_test")
            .withUsername("test")
            .withPassword("test");

    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName
            .parse("redis:7-alpine"))
            .withExposedPorts(6379);

    static final NatsContainer nats = new NatsContainer(DockerImageName
            .parse("nats:2.14.3-alpine"))
            .withExposedPorts(4222)
            .withJetStream();

    static {
        postgres.start();
        redis.start();
        nats.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // postgres
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));

        // Nats
        registry.add("broker.url", nats::getConnectionUrl);
    }
}