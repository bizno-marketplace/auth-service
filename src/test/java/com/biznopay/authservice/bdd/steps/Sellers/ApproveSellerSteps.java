package com.biznopay.authservice.bdd.steps.Sellers;

import com.biznopay.authservice.bdd.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.testcases.SuperAdminTestCases;
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
import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.BuyerTestCases.validBuyer;
import static com.biznopay.authservice.testcases.SellerTestCases.*;

public class ApproveSellerSteps {
    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

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

    //    SCENARIO: Successfully approve a new seller registration
    @Given("an existing seller and with status {string}")
    public void aSellerExistIdAndStatus(String status) {
        UserId VALID_USER_ID = UserId.of(UUID.randomUUID());
        User seller = Seller.reconstruct(VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, UserStatus.AWAITING_APPROVAL, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS_NEW, VALID_BI);;
        UserJpaEntity userJpa = UserMapper.toUserJpaEntity(seller);
        userJpa.setStatus(UserStatus.valueOf(status));
        userJpa = userJpaRepository.save(userJpa);
        Assertions.assertEquals(status, userJpa.getStatus().name());
        scenarioContext.getHeadersMap().put("sellerId", userJpa.getId().toString());
    }

    @And("i am authenticated as a Supper Admin")
    public void iAmAuthenticatedAsASupperAdmin() {
        UserJpaEntity userJpa = UserMapper.toUserJpaEntity(SuperAdminTestCases.VALID_SUPER_ADMIN);
        userJpaRepository.save(userJpa);
        String authToken = jwtHelper.generate(userJpa.getEmail());
        scenarioContext.getHeadersMap().put("token", authToken);
    }

    @When("i send a PATCH request to {string}")
    public void iSendAPatchRequestTo(String path) {
        path = path.replace("sellerId", scenarioContext.getHeadersMap().get("sellerId"));
        String token = scenarioContext.getHeadersMap().get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<ApiResponse> response = scenarioContext.getRestTemplate()
                .exchange(this.scenarioContext.url(path), HttpMethod.PATCH, request, ApiResponse.class);
        scenarioContext.setResponse(response);
    }

    @And("the seller status should be {string}")
    public void theSellerStatusShouldBe(String expectedStatus) {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> user = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(user.isPresent());
        UserJpaEntity userJpa = user.get();
        Assertions.assertEquals(expectedStatus, userJpa.getStatus().name());
    }

    //    SCENARIO: Reject approval if seller does not exist
    @Given("no seller exists with id {string}")
    public void noSellerExistsWithId(String sellerId) {
        scenarioContext.getHeadersMap().put("sellerId", sellerId);
    }

    // SCENARIO: Reject approval if requester is not a Supper Admin
    @And("i'm authenticated as a Buyer")
    public void imAuthenticatedBuyer() {
        User buyer = validBuyer("joana.tembe@gmail.com");
        buyer.setToAwaitingForApproval();
        UserJpaEntity userJpa = UserMapper.toUserJpaEntity(buyer);
        userJpaRepository.save(userJpa);
        String authToken = jwtHelper.generate(userJpa.getEmail());
        scenarioContext.getHeadersMap().put("token", authToken);
    }
}