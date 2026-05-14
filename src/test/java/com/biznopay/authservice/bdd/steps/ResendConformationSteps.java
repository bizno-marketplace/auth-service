package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.ResendConfirmationRequest;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.mocks.Mocks;
import com.netflix.discovery.converters.Auto;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
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
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ResendConformationSteps {
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
    }

//  COMOM
    private UserJpaEntity entity;
    private ActivationTokenJpaEntity previousActivationToken;

    @Then("the response status should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        Assertions.assertEquals(statusCode, response.getStatusCode().value());
    }

    @And("the response body should contain error {string}")
    public void theResponseBodyShouldContainError(String error) {
        Assertions.assertEquals(error, response.getBody().error().message());
    }

//  SCENARIO: Successfully resend confirmation email for a pending account
    @Given("a user registered with email {string} with status {string}")
    public void aUserRegisteredWithStatus(String email, String status) {
        entity = Mocks.buyerJpaEntityMock();
        entity.setEmail(email);
        entity.setStatus(UserStatus.valueOf(status));
        userJpaRepository.save(entity);
    }

    @And("the previous confirmation token has expired")
    public void thePreviousConfirmationTokenHasExpired() {
        previousActivationToken =  Mocks.unusedActivationTokenJpaEntityFromBuyerMock(entity);
        previousActivationToken.setExpiresAt(LocalDateTime.now().minusMinutes(15));
        activationTokenJpaRepository.save(previousActivationToken);
    }

    @When("i send a POST request to {string} with body:")
    public void iSendAPOSTRequestToWithBody(String path, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        ResendConfirmationRequest request = new ResendConfirmationRequest(data.get("email"));
        response = restTemplate.postForEntity(url(path),request,ApiResponse.class);
    }

    @And("the previous token should be invalidated")
    public void thePreviousTokenShouldBeInvalidated() {
        UUID activationTokenId = previousActivationToken.getId();
        Optional<ActivationTokenJpaEntity> opToken =  activationTokenJpaRepository.findById(activationTokenId);
        Assertions.assertTrue(opToken.isEmpty());
    }

    @And("a new confirmation email should be sent to {string}")
    public void aNewConfirmationEmailShouldBeSentTo(String email) {
        Optional<ActivationTokenJpaEntity> opToken =  activationTokenJpaRepository.findByUsedAndUserId(false,entity.getId());
        Assertions.assertTrue(opToken.isPresent());
    }

    @And("the new token should expire in 15 minutes")
    public void theNewTokenShouldExpireIn15Minutes() {
        Optional<ActivationTokenJpaEntity> opToken =  activationTokenJpaRepository.findByUsedAndUserId(false,entity.getId());
        Assertions.assertTrue(opToken.isPresent());
        Assertions.assertEquals(opToken.get().getCreatedAt(), opToken.get().getExpiresAt().minusMinutes(15));
    }

    @And("the response body should contain message {string}")
    public void theResponseBodyShouldContainMessage(String message) {
        Assertions.assertEquals(message, response.getBody().data());
    }

//  SCENARIO: Reject resend when account is already active
    @Given("a user with email {string} has status {string}")
    public void aUserEmailHasStatus(String email, String status){
        entity = Mocks.buyerJpaEntityMock();
        entity.setEmail(email);
        entity.setStatus(UserStatus.valueOf(status));
        userJpaRepository.save(entity);
    }
}
