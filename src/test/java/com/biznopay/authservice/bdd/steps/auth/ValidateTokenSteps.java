package com.biznopay.authservice.bdd.steps.auth;

import com.biznopay.authservice.bdd._config.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.grpc.AuthServiceGrpc;
import com.biznopay.authservice.grpc.ValidateTokenRequest;
import com.biznopay.authservice.grpc.ValidateTokenResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.AddressJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
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
import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.SuperAdminTestCases.*;


public class ValidateTokenSteps {
    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AddressJpaRepository addressJpaRepository;

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

    @Given("an active user exists in the database")
    public void anActiveUserExistsInTheDatabase(){
        UUID userId = UserId.of(UUID.randomUUID()).value();
        UserJpaEntity entity =  new SuperAdminJpaEntity();
        entity.setId(userId);
        entity.setFirstName(VALID_FIRST_NAME);
        entity.setLastName(VALID_LAST_NAME);
        entity.setEmail(VALID_EMAIL);
        entity.setPhone(VALID_PHONE);
        entity.setPassword(VALID_PASSWORD);
        entity.setExpiresAt(VALID_EXPIRES_AT);
        entity.setCreatedAt(VALID_CREATED_AT);
        entity.setUpdatedAt(VALID_UPDATED_AT);
        entity.setStatus(UserStatus.ACTIVE);
        entity = userJpaRepository.save(entity);
        scenarioContext.getHeadersMap().put("userId", entity.getId().toString());
    }

    @And("a valid JWT token is generated for that user")
    public void aValidJWTTokenIsGeneratedForThatUser(){
        UUID userId = UUID.fromString(scenarioContext.getHeadersMap().get("userId"));
        Optional<UserJpaEntity> entityOpt = userJpaRepository.findById(userId);
        Assertions.assertTrue(entityOpt.isPresent());
        UserJpaEntity entity =  entityOpt.get();
        String token = jwtHelper.generate(entity.getPassword(),entity.getRole(), entity.getStatus().name(), entity.getEmail());
        scenarioContext.getHeadersMap().put("token", token);
    }

    @When("the ValidateToken gRPC method is called with the token")
    public void theValidateTokenGRPCMethodIsCalledWithTheToken(){
        String token = scenarioContext.getHeadersMap().get("token");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();

        AuthServiceGrpc.AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel);

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken(token)
                .build();

        ValidateTokenResponse response = stub.validateToken(request);
        scenarioContext.getHeadersMap().put("grpcValid", String.valueOf(response.getValid()));
        channel.shutdown();
    }

    @Then("the gRPC response valid field should be {string}")
    public void theGRPCResponseValidFieldShouldBeTrue(String status){
        String isValid = scenarioContext.getHeadersMap().get("grpcValid");
        Assertions.assertEquals(status, isValid);
    }

    @When("the ValidateToken gRPC method is called with an empty token")
    public void theValidateTokenGRPCMethodIsCalledWithAnEmptyToken(){
        String token = "";

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();

        AuthServiceGrpc.AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel);

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken(token)
                .build();

        ValidateTokenResponse response = stub.validateToken(request);
        scenarioContext.getHeadersMap().put("grpcValid", String.valueOf(response.getValid()));
        channel.shutdown();
    }

    @When("the ValidateToken gRPC method is called with an invalid token")
    public void theValidateTokenGRPCMethodIsCalledWithAnInvalidToken(){
        String token = UUID.randomUUID().toString();

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", grpcPort)
                .usePlaintext()
                .build();

        AuthServiceGrpc.AuthServiceBlockingStub stub = AuthServiceGrpc.newBlockingStub(channel);

        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken(token)
                .build();

        ValidateTokenResponse response = stub.validateToken(request);
        scenarioContext.getHeadersMap().put("grpcValid", String.valueOf(response.getValid()));
        channel.shutdown();
    }
}
