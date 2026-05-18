package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.bdd.ScenarioContext;
import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.ActivationTokenMapper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.mocks.Mocks;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

public class ConfirmAccountSteps {

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

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

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
        scenarioContext.setRestTemplate(restTemplate);
        jdbcTemplate.execute("TRUNCATE TABLE t_activation_tokens RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE t_users RESTART IDENTITY CASCADE");
    }

    // SCENARIO: Successfully confirm account with valid token
    @Given("a user registered with email {string} has a valid confirmation token")
    public void aUserRegisteredWithEmailHasAValidConfirmationToken(String email) {
        Address address = Mocks.addressMock();
        User user = Buyer.register("John", "Smith", email, "848484848", "Password@123", address);
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);

        ActivationToken activationToken = ActivationToken.generate(user.getId());
        ActivationTokenJpaEntity activationTokenEntity = ActivationTokenMapper.toJpaEntity(activationToken);
        activationTokenJpaRepository.save(activationTokenEntity);
    }

    @When("i send a confirmation request with the valid token")
    public void iSendAConfirmationRequestWithTheValidToken() {
        ActivationTokenJpaEntity token = activationTokenJpaRepository.findAll().get(0);
        scenarioContext.setResponse(
                scenarioContext.getRestTemplate().getForEntity(
                        url("/accounts/confirm-account?token=" + token.getId()),
                        ApiResponse.class
                )
        );
    }

    @And("the user account status should be {string}")
    public void theUserAccountStatusShouldBe(String expectedStatus) {
        UserJpaEntity userEntity = userJpaRepository.findAll().get(0);
        Assertions.assertEquals(expectedStatus, userEntity.getStatus().name());
    }

    // SCENARIO: Reject confirmation with expired token
    @Given("a user registered with email {string} has an expired confirmation token")
    public void aUserRegisteredWithEmailHasAnExpiredConfirmationToken(String email) {
        Address address = Mocks.addressMock();
        User user = Buyer.register("John", "Smith", email, "848484848", "Password@123", address);
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);

        ActivationToken activationToken = ActivationToken.generate(user.getId());
        ActivationTokenJpaEntity activationTokenEntity = ActivationTokenMapper.toJpaEntity(activationToken);
        activationTokenEntity.setExpiresAt(LocalDateTime.now().minusMinutes(15));
        activationTokenJpaRepository.save(activationTokenEntity);
    }

    // SCENARIO: Reject confirmation with invalid or tampered token
    @Given("a user registered with email {string}")
    public void aUserRegisteredWithEmail(String email) {
        Address address = Mocks.addressMock();
        User user = Buyer.register("John", "Smith", email, "848484848", "Password@123", address);
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }

    @When("i send a confirmation request with the invalid token")
    public void iSendAConfirmationRequestWithTheInvalidToken() {
        scenarioContext.setResponse(
                scenarioContext.getRestTemplate().getForEntity(
                        url("/accounts/confirm-account?token=invalidToken"),
                        ApiResponse.class
                )
        );
    }

    // SCENARIO: Reject confirmation when account is already active
    @Given("a user with email {string} has already confirmed the account")
    public void aUserWithEmailHasAlreadyConfirmedTheAccount(String email) {
        Address address = Mocks.addressMock();
        User user = Buyer.register("John", "Smith", email, "848484848", "Password@123", address);
        user.activate();
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);

        ActivationToken activationToken = ActivationToken.generate(user.getId());
        ActivationTokenJpaEntity activationTokenEntity = ActivationTokenMapper.toJpaEntity(activationToken);
        activationTokenJpaRepository.save(activationTokenEntity);
    }

    // SCENARIO: Reject confirmation when token is missing
    @When("i send a confirmation request with the missing token")
    public void iSendAConfirmationRequestWithTheMissingToken() {
        scenarioContext.setResponse(
                scenarioContext.getRestTemplate().getForEntity(
                        url("/accounts/confirm-account?token="),
                        ApiResponse.class
                )
        );
    }
}