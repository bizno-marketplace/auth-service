package com.biznopay.authservice.config;

import io.nats.client.Connection;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public Connection natsConnection() {
        return Mockito.mock(Connection.class);
    }
}