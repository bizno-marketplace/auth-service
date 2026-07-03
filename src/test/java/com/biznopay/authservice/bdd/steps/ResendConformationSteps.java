package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.bdd.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.gateway.ResendCooldownGateway;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.biznopay.authservice.testcases.ActivationTokenTestCases.activationTokenJpa;
import static com.biznopay.authservice.testcases.BuyerTestCases.validBuyer;

public class ResendConformationSteps {

    @Autowired
    StringRedisTemplate redisTemplate;
    @LocalServerPort
    private int port;
    @Autowired
    private ScenarioContext scenarioContext;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ActivationTokenJpaRepository activationTokenJpaRepository;
    @Autowired
    private ResendCooldownGateway resendCooldownGateway;
    private UserJpaEntity entity;
    private ActivationTokenJpaEntity previousActivationToken;

    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            }
        });
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        scenarioContext.setRestTemplate(restTemplate);
        scenarioContext.setRedisTemplate(redisTemplate);
        jdbcTemplate.execute("TRUNCATE TABLE t_activation_tokens RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE t_users RESTART IDENTITY CASCADE");
    }


    // SCENARIO: Successfully resend confirmation email for a pending account
    @Given("a user registered with email {string} with status {string}")
    public void aUserRegisteredWithStatus(String email, String status) {
        User user = validBuyer(email);
        entity = UserMapper.toUserJpaEntity(user);
        entity.setEmail(email);
        entity.setStatus(UserStatus.valueOf(status));
        userJpaRepository.save(entity);
    }

    @And("the previous confirmation token has expired")
    public void thePreviousConfirmationTokenHasExpired() {
        previousActivationToken = activationTokenJpa(entity.getId());
        previousActivationToken.setExpiresAt(LocalDateTime.now().minusMinutes(15));
        activationTokenJpaRepository.save(previousActivationToken);
    }

    @And("the previous token should be invalidated")
    public void thePreviousTokenShouldBeInvalidated() {
        Optional<ActivationTokenJpaEntity> opToken =
                activationTokenJpaRepository.findById(previousActivationToken.getId());
        Assertions.assertTrue(opToken.isEmpty());
    }

    @And("a new confirmation email should be sent to {string}")
    public void aNewConfirmationEmailShouldBeSentTo(String email) {
        Optional<ActivationTokenJpaEntity> opToken =
                activationTokenJpaRepository.findByUsedAndUserId(false, entity.getId());
        Assertions.assertTrue(opToken.isPresent());
    }

    @And("the new token should expire in 15 minutes")
    public void theNewTokenShouldExpireIn15Minutes() {
        Optional<ActivationTokenJpaEntity> opToken =
                activationTokenJpaRepository.findByUsedAndUserId(false, entity.getId());
        Assertions.assertTrue(opToken.isPresent());
        Assertions.assertEquals(
                opToken.get().getCreatedAt(),
                opToken.get().getExpiresAt().minusMinutes(15)
        );
    }

    // SCENARIO: Reject resend when account is already active
    @Given("a user with email {string} has status {string}")
    public void aUserEmailHasStatus(String email, String status) {
        User user = validBuyer(email);
        entity = UserMapper.toUserJpaEntity(user);
        entity.setEmail(email);
        entity.setStatus(UserStatus.valueOf(status));
        userJpaRepository.save(entity);
    }

    // SCENARIO: Return 200 for non-existent email (security — no enumeration)
    @Given("no user exists with email {string}")
    public void noUserExistsWithEmail(String email) {
        // setup method does it
    }

    // SCENARIO: Reject resend during cooldown period
    @And("a confirmation email was already sent less than {int} minutes ago")
    public void aConfirmationEmailWasAlreadySentLessThanMinutesAgo(int minutes) {
        previousActivationToken = activationTokenJpa(entity.getId());
        activationTokenJpaRepository.save(previousActivationToken);
        resendCooldownGateway.startCooldown(entity.getEmail(), Duration.ofMinutes(minutes));
    }
}
