package com.biznopay.authservice.bdd.steps.auth;

import com.biznopay.authservice.bdd._config.ScenarioContext;
import com.biznopay.authservice.grpc.AuthServiceGrpc;
import com.biznopay.authservice.grpc.GetUserProfileRequest;
import com.biznopay.authservice.grpc.GetUserProfileResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

public class getUserProfile {
    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Value("${spring.grpc.server.port}")
    private int grpcPort;

    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
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

    @When("the GetUserProfile gRPC method is called with the user id")
    public void theGetUserProfileMethodIsCalledWithTheUserId() {
        String userId = scenarioContext.getHeadersMap().get("userId");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();

        AuthServiceGrpc.AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel);

        GetUserProfileRequest request = GetUserProfileRequest.newBuilder()
                .setUserId(userId)
                .build();

        GetUserProfileResponse response = stub.getUserProfile(request);
        scenarioContext.getHeadersMap().put("userId", response.getUserId());
        scenarioContext.getHeadersMap().put("email", response.getEmail());
        scenarioContext.getHeadersMap().put("firstName", response.getFirstName());
        scenarioContext.getHeadersMap().put("lastName", response.getLastName());
        scenarioContext.getHeadersMap().put("role", response.getRole());
        scenarioContext.getHeadersMap().put("status", response.getStatus());
        channel.shutdown();
    }

    @Then("the response should contain the user id")
    public void theResponseShouldContainTheUserId() {
        String userId = scenarioContext.getHeadersMap().get("userId");
        Assertions.assertNotNull(userId);
        Assertions.assertFalse(userId.isBlank());
    }

    @And("the response should contain the user email")
    public void theResponseShouldContainTheUserEmail() {
        String email = scenarioContext.getHeadersMap().get("email");
        Assertions.assertNotNull(email);
        Assertions.assertFalse(email.isBlank());
    }

    @And("the response should contain the user first name")
    public void theResponseShouldContainTheUserFirstName() {
        String firstName = scenarioContext.getHeadersMap().get("firstName");
        Assertions.assertNotNull(firstName);
        Assertions.assertFalse(firstName.isBlank());
    }

    @And("the response should contain the user last name")
    public void theResponseShouldContainTheUserLastName() {
        String lastName = scenarioContext.getHeadersMap().get("lastName");
        Assertions.assertNotNull(lastName);
        Assertions.assertFalse(lastName.isBlank());
    }

    @And("the response should contain the user role")
    public void theResponseShouldContainTheUserRole() {
        String role = scenarioContext.getHeadersMap().get("role");
        Assertions.assertNotNull(role);
        Assertions.assertFalse(role.isBlank());
    }

    @And("the response should contain the user status {string}")
    public void theResponseShouldContainTheUserStatus(String expectedStatus) {
        String status = scenarioContext.getHeadersMap().get("status");
        Assertions.assertNotNull(status);
        Assertions.assertEquals(expectedStatus, status);
    }
}
