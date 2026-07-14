package com.biznopay.authservice.bdd.steps.user.Seller;

import com.biznopay.authservice.bdd._config.ScenarioContext;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.AddressJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.BuyerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.AddressJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.SellerRejectionJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.UpdateSellerRequest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.BuyerTestCases.VALID_BUYER_JPA;
import static com.biznopay.authservice.testcases.SellerTestCases.*;

public class UpdateSellerSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AddressJpaRepository addressJpaRepository;

    @Autowired
    private SellerRejectionJpaRepository sellerRejectionRepository;

    @Autowired
    private JwtHelper jwtHelper;

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
        scenarioContext.setJdbcTemplate(jdbcTemplate);
        jdbcTemplate.execute("TRUNCATE TABLE t_activation_tokens RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE t_users RESTART IDENTITY CASCADE");
    }

    //  Scenario: Reject update when no authentication token is provided
    @When("i send a PATCH request to update seller using endpoint {string} without an authorization header")
    public void iSendPatchRequestToUpdateSellerUsingEndpointWithoutAnAuthorizationHeader(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + " ");
        UpdateSellerRequest body = new UpdateSellerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE,
                VALID_STORE_NAME, VALID_STORE_DESC);

        HttpEntity<UpdateSellerRequest> request = new HttpEntity<>(body, headers);
        ResponseEntity<ApiResponse> response = scenarioContext.getRestTemplate()
                .exchange(this.scenarioContext.url(path),
                        HttpMethod.PATCH,
                        request,
                        ApiResponse.class);
        scenarioContext.setResponse(response);
    }

    //    Scenario: Reject update when logged user is not a Seller
    @Given("a Buyer is authenticated")
    public void aBuyerIsAuthenticated() {
        BuyerJpaEntity entity = (BuyerJpaEntity) VALID_BUYER_JPA();
        AddressJpaEntity addressJpa = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpa);
        entity.setDeliveryAddresses(List.of(addressJpa));
        userJpaRepository.save(entity);
        String token = jwtHelper.generate(entity.getPassword(), entity.getRole(), entity.getStatus().name(), entity.getEmail());
        scenarioContext.getHeadersMap().put("token", token);
    }

    @When("i send a PATCH request to update seller using endpoint {string} with:")
    public void iSendPatchRequestToUpdateSellerUsingEndpointWith(String path, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String storeName = data.get("storeName") != null ? data.get("storeName") : VALID_STORE_NAME;
        String storeDesc = data.get("storeDescription") != null ? data.get("storeDescription") : VALID_STORE_DESC;
        String email = data.get("email") != null ? data.get("email") : VALID_EMAIL;

        UpdateSellerRequest body = new UpdateSellerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, email, VALID_PHONE,
                storeName, storeDesc);

        HttpHeaders headers = new HttpHeaders();
        String token = scenarioContext.getHeadersMap().get("token");
        ;
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse> response = scenarioContext.getRestTemplate()
                .exchange(this.scenarioContext.url(path),
                        HttpMethod.PATCH,
                        new HttpEntity<>(body, headers),
                        ApiResponse.class);
        scenarioContext.setResponse(response);
    }

    //      Scenario: Successfully update store fields without changing email
    @Given("a seller exists with status {string}")
    public void aSellerExistsWithStatus(String status) {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        AddressJpaEntity addressJpa = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpa);
        entity.setStoreAddress(addressJpa);
        entity.setStatus(UserStatus.valueOf(status));
        userJpaRepository.save(entity);
        scenarioContext.getHeadersMap().put("sellerId", entity.getId().toString());
    }

    @And("i am authenticated as that seller")
    public void iAmAuthenticatedAsThatSeller() {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> entityOpt = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(entityOpt.isPresent());
        UserJpaEntity entity = entityOpt.get();
        String token = jwtHelper.generate(entity.getPassword(), entity.getRole(), entity.getStatus().name(), entity.getEmail());
        scenarioContext.getHeadersMap().put("token", token);
    }

    //   Scenario: Keep unprovided fields unchanged after partial update
    @Given("a seller exists with status {string} and first name {string}")
    public void aSellerExistsWithStatusAndFirstName(String status, String firstName) {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        entity.setFirstName(firstName);
        AddressJpaEntity addressJpa = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpa);
        entity.setStoreAddress(addressJpa);
        entity.setStatus(UserStatus.valueOf(status));
        userJpaRepository.saveAndFlush(entity);
        scenarioContext.getHeadersMap().put("sellerId", entity.getId().toString());
        scenarioContext.getHeadersMap().put("firstName", firstName);
    }

    @And("the seller first name should remain unchanged")
    public void theSellerFirstNameShouldRemainUnchanged() {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        String firstName = scenarioContext.getHeadersMap().get("firstName");
        Optional<UserJpaEntity> entityOpt = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(entityOpt.isPresent());
        UserJpaEntity entity = entityOpt.get();
        Assertions.assertEquals(firstName, entity.getFirstName());
    }

    @Given("a seller exists with status {string} and email {string}")
    public void aSellerExistsWithStatusAndEmail(String status, String email) {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        entity.setEmail(email);
        AddressJpaEntity addressJpa = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpa);
        entity.setStoreAddress(addressJpa);
        entity.setStatus(UserStatus.valueOf(status));
        userJpaRepository.saveAndFlush(entity);
        scenarioContext.getHeadersMap().put("sellerId", entity.getId().toString());
        scenarioContext.getHeadersMap().put("email", email);
    }
}
