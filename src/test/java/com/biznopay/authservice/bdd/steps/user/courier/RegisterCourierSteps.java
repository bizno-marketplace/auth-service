package com.biznopay.authservice.bdd.steps.user.courier;

import com.biznopay.authservice.bdd._config.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.VehicleTypeEnum;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.*;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.OutboxEventJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.RegisterCourierRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.CourierTestCases.*;
import static com.biznopay.authservice.testcases.SellerTestCases.VALID_ADDRESS_NEW;
import static com.biznopay.authservice.testcases.SellerTestCases.VALID_SELLER_JPA;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN_JPA;

public class RegisterCourierSteps {
    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ActivationTokenJpaRepository activationTokenJpaRepository;
    @Autowired
    private OutboxEventJpaRepository outboxEventJpaRepository;

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

    //      Scenario: Reject registration when no authentication token is provided
    @When("i send a POST request register courier using endpoint {string} without an Authorization header")
    public void iSendAPOSTRequestRegisterCourierUsingEndpointWithoutAnAuthorizationHeader(String endpoint) {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);
        ResponseEntity<ApiResponse> response = scenarioContext.getRestTemplate()
                .exchange(
                        scenarioContext.url("/couriers/register"),
                        HttpMethod.POST,
                        new HttpEntity<>(request),
                        new ParameterizedTypeReference<ApiResponse>() {
                        }
                );
        scenarioContext.setResponse(response);
    }

    //      Scenario: Reject registration when logged user is not a Super Admin
    @Given("a seller is authenticated")
    public void aSellerIsAuthenticated() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        entity.setStoreAddress(addressJpaEntity);
        entity = userJpaRepository.save(entity);
        User user = UserMapper.toUserDomain(entity);
        String token = scenarioContext.getJwtHelper().generate(user);
        scenarioContext.getHeadersMap().put("token", token);
    }

    @When("i send a POST request register courier using endpoint {string} with:")
    public void iSendAPOSTRequestRegisterCourierUsingEndpointWith(String path, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        RegisterCourierRequest request = new RegisterCourierRequest(
                data.get("firstName"), data.get("lastName"),
                data.get("email"), data.get("phone"),
                data.get("password"), VehicleTypeEnum.valueOf(data.get("vehicleType")),
                data.get("licenseNumber"), data.get("zone"));

        String token = scenarioContext.getHeadersMap().get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse> response = scenarioContext.getRestTemplate()
                .exchange(
                        scenarioContext.url(path),
                        HttpMethod.POST,
                        new HttpEntity<>(request, headers),
                        new ParameterizedTypeReference<ApiResponse>() {
                        }
                );

        scenarioContext.setResponse(response);
        scenarioContext.getHeadersMap().put("email", request.email());
    }

    //      Scenario: Successfully register a courier
    @And("a courier should exist with status {string}")
    public void aCourierShouldExistWithStatus(String status) {
        String email = scenarioContext.getHeadersMap().get("email");
        Optional<UserJpaEntity> entityOpt = userJpaRepository.findByEmail(email);
        Assertions.assertTrue(entityOpt.isPresent());
        CourierJpaEntity entity = (CourierJpaEntity) entityOpt.get();
        Assertions.assertEquals(status, entity.getStatus().name());
        scenarioContext.getHeadersMap().put("courierId", entity.getId().toString());
    }

    @And("an activation token should be generated for the courier")
    public void anActivationTokenShouldBeGeneratedForTheCourier() {
        UUID courierId = UUID.fromString(scenarioContext.getHeadersMap().get("courierId"));
        Optional<ActivationTokenJpaEntity> entityOpt = activationTokenJpaRepository.findByUsedAndUserId(false, courierId);
        Assertions.assertTrue(entityOpt.isPresent());
    }

    @And("a domain event should be published to notify the courier registration")
    public void aDomainEventShouldBePublishedToNotifyCourierRegistration() {
        UUID courierId = UUID.fromString(scenarioContext.getHeadersMap().get("courierId"));
        Optional<OutboxEventJpaEntity> outboxEventJpaEntityOpt = outboxEventJpaRepository.findByAggregateId(courierId);
        Assertions.assertTrue(outboxEventJpaEntityOpt.isPresent());
    }

    //      Scenario: Reject registration when email already exists
    @Given("a user already exists with email {string}")
    public void aUserAlreadyExistsWithEmail(String email) {
        UserJpaEntity entity = VALID_SUPER_ADMIN_JPA;
        entity.setEmail(email);
        userJpaRepository.save(entity);
    }
}
