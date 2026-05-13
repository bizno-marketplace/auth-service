package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.ActivationTokenMapper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

public class ConfirmAccountSteps {
    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<ApiResponse> response;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ActivationTokenJpaRepository activationTokenJpaRepository;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Before
    public void setUp() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
            }
        });
        jdbcTemplate.execute("TRUNCATE TABLE t_users RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE t_activation_tokens RESTART IDENTITY CASCADE");
    }

//  COMMON STEPS
    @When("i send a confirmation request with the valid token")
    public void iSendAConfirmationRequestWithTheValidToken() {
        ActivationTokenJpaEntity activationTokenEntity = activationTokenJpaRepository.findAll().get(0);
        String token = activationTokenEntity.getId().toString();
        response = restTemplate.getForEntity(url("/accounts/confirm-account?token=" + token), ApiResponse.class);
    }

    @Then("the response code is {int}")
    public void theResponseCodeIs(int expectedStatus) {
        Assertions.assertEquals(expectedStatus, response.getStatusCode().value());
    }

    @And("the response body should contain error {string}")
    public void theResponseBodyShouldContainError(String message) {
        Assertions.assertEquals(message, response.getBody().error().message());
    }

//  SCENARIO: Successfully confirm account with valid token
    @Given("a user registered with email {string} has a valid confirmation token")
    public void aUserRegisteredWithEmailHasAValidConfirmationToken(String email) {
        User user = Buyer.register("John", "Smith", email, "Password@123");
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);

        ActivationToken activationToken = ActivationToken.generate(user.getId());
        ActivationTokenJpaEntity activationTokenEntity = ActivationTokenMapper.toJpaEntity(activationToken);
        activationTokenJpaRepository.save(activationTokenEntity);
    }

    @And("the user account status should be {string}")
    public void theUserAccountStatusShouldBe(String expectedStatus) {
        UserJpaEntity userEntity = userJpaRepository.findAll().get(0);
        Assertions.assertEquals(expectedStatus, userEntity.getStatus().name());
    }

//  SCENARIO: Reject confirmation with expired token
    @Given("a user registered with email {string} has an expired confirmation token")
    public void aUserRegisteredWithEmailHasAnExpiredConfirmationToken(String email){
        User user = Buyer.register("John", "Smith", email, "Password@123");
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);

        ActivationToken activationToken = ActivationToken.generate(user.getId());
        ActivationTokenJpaEntity activationTokenEntity = ActivationTokenMapper.toJpaEntity(activationToken);
        activationTokenEntity.setExpiresAt(LocalDateTime.now().minusMinutes(15));
        activationTokenJpaRepository.save(activationTokenEntity);
    }

    @Given("a user registered with email {string}")
    public void aUserRegisteredWithEmail(String email) {
        User user = Buyer.register("John", "Smith", email, "Password@123");
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }

    @When("i send a confirmation request with the invalid token")
    public void iSendAConfirmationRequestWithTheInvalidToken() {
        response = restTemplate.getForEntity(url("/accounts/confirm-account?token=invalidToken"), ApiResponse.class);
    }
}