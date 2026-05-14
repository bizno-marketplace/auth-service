package com.biznopay.authservice.infra.controller;

import com.biznopay.authservice.config.ContainerBase;
import com.biznopay.authservice.config.TestConfig;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.ResendConfirmationRequest;
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

import java.time.LocalDateTime;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerTests extends ContainerBase {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ActivationTokenJpaRepository activationTokenJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
        jdbcTemplate.execute("TRUNCATE TABLE t_activation_tokens RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE t_users RESTART IDENTITY CASCADE");
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    @DisplayName("Should return 204 on successful account confirmation")
    void shouldReturn204OnSuccessfulAccountConfirmation() {
        UserJpaEntity user = Mocks.buyerJpaEntityMock();
        userJpaRepository.save(user);
        ActivationTokenJpaEntity entity = Mocks.unusedActivationTokenJpaEntityFromBuyerMock(user);
        activationTokenJpaRepository.save(entity);
        ResponseEntity response = restTemplate.getForEntity(url("/accounts/confirm-account?token=" + entity.getId().toString()), Void.class);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 410 on expired token")
    public void shouldReturn410OnExpiredToken() {
        UserJpaEntity user = Mocks.buyerJpaEntityMock();
        userJpaRepository.save(user);
        ActivationTokenJpaEntity entity = Mocks.unusedActivationTokenJpaEntityFromBuyerMock(user);
        entity.setExpiresAt(LocalDateTime.now().minusMinutes(15));
        activationTokenJpaRepository.save(entity);
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url("/accounts/confirm-account?token=" + entity.getId()), ApiResponse.class);
        Assertions.assertEquals(HttpStatus.GONE, response.getStatusCode());
        Assertions.assertEquals("Confirmation link expired", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 400 on invalid token")
    void shouldReturn400OnInvalidToken() {
        UserJpaEntity user = Mocks.buyerJpaEntityMock();
        userJpaRepository.save(user);
        ActivationTokenJpaEntity entity = Mocks.unusedActivationTokenJpaEntityFromBuyerMock(user);
        activationTokenJpaRepository.save(entity);
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url("/accounts/confirm-account?token=" + user.getId()), ApiResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Invalid confirmation link", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 409 on account already active")
    public void shouldReturn409OnAccountAlreadyActive() {
        UserJpaEntity user = Mocks.buyerJpaEntityMock();
        user.setStatus(UserStatus.ACTIVE);
        userJpaRepository.save(user);
        ActivationTokenJpaEntity entity = Mocks.unusedActivationTokenJpaEntityFromBuyerMock(user);
        entity.setUsed(true);
        activationTokenJpaRepository.save(entity);
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url("/accounts/confirm-account?token=" + entity.getId()), ApiResponse.class);
        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertEquals("Account already confirmed", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 400 when token is missing")
    void shouldReturn400WhenTokenIsMissing() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url("/accounts/confirm-account?token="), ApiResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Token is required", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 200 on successfully resend confirmation email for a pending account on resend confirmation")
    public void shouldReturn200OnSuccessfullyResendConfirmationEmailForAPendingAccountOnResendConfirmation(){
        UserJpaEntity user = Mocks.buyerJpaEntityMock();
        userJpaRepository.save(user);
        ActivationTokenJpaEntity entity = Mocks.unusedActivationTokenJpaEntityFromBuyerMock(user);
        activationTokenJpaRepository.save(entity);
        ResendConfirmationRequest request =  new ResendConfirmationRequest(user.getEmail());
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/accounts/resend-confirmation"),request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
