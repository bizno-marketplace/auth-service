package com.biznopay.authservice.infra.controller;

import com.biznopay.authservice.config.PostgresContainerBase;
import com.biznopay.authservice.config.TestConfig;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.mocks.Mocks;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerTests extends PostgresContainerBase {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ActivationTokenJpaRepository activationTokenJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterAll
    static void tearDown() {
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
        jdbcTemplate.execute("TRUNCATE TABLE t_activation_tokens RESTART IDENTITY CASCADE");
    }

    @Test
    @DisplayName("Should return 204 on successful account confirmation")
    void shouldReturn204OnSuccessfulAccountConfirmation() {
        UserJpaEntity user = Mocks.buyerJpaEntityMock();
        userJpaRepository.save(user);
        ActivationTokenJpaEntity entity = Mocks.unusedActivationTokenJpaEntityFromBuyerMock(user);
        activationTokenJpaRepository.save(entity);
        ResponseEntity response = restTemplate.getForEntity(url("/accounts?token=" + entity.getId()), Void.class);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
