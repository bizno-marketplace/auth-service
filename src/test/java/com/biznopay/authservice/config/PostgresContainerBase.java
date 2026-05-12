package com.biznopay.authservice.config;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public abstract class PostgresContainerBase {

    @Container
    @ServiceConnection
    public static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:latest")
    );

    static {
        postgres.start();
    }
}