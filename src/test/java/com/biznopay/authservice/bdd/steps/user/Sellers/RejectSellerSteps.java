package com.biznopay.authservice.bdd.steps.user.Sellers;

import com.biznopay.authservice.bdd._config.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerRejectionJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.SellerRejectionJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.RejectSellerRequest;
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

import static com.biznopay.authservice.testcases.SellerTestCases.*;

public class RejectSellerSteps {
    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

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

    //    SCENARIO: Successfully reject a pending seller
    @Given("a existing seller with status {string} and rejection count {int}")
    public void aExistingSellerWithStatusAndRejection(String status, int rejectionCount) {
        UserId VALID_USER_ID = UserId.of(UUID.randomUUID());
        User seller = Seller.reconstruct(VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, UserStatus.AWAITING_APPROVAL, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS_NEW, VALID_BI);
        UserJpaEntity userJpa = UserMapper.toUserJpaEntity(seller);
        userJpa.setStatus(UserStatus.valueOf(status));
        userJpa = userJpaRepository.save(userJpa);
        Assertions.assertEquals(status, userJpa.getStatus().name());
        scenarioContext.getHeadersMap().put("sellerId", userJpa.getId().toString());
        scenarioContext.getHeadersMap().put("rejectionCount", String.valueOf(rejectionCount));
    }

    @When("i send a PATCH request to reject seller using endpoint {string}")
    public void iSendAPatchRequestToRejectSellerUsingEndpoint(String path, DataTable dataTable) {
        path = path.replace("sellerId", scenarioContext.getHeadersMap().get("sellerId"));
        String token = scenarioContext.getHeadersMap().get("token");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        Map<String, String> data = dataTable.asMap(String.class, String.class);
        RejectSellerRequest body = new RejectSellerRequest(data.get("reason"));
        int rejectionCount = Integer.parseInt(scenarioContext.getHeadersMap().get("rejectionCount"));
        if (rejectionCount > 0) {
            UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
            SellerJpaEntity sellerJpaEntity = new SellerJpaEntity();
            sellerJpaEntity.setId(sellerId);

            SellerRejectionJpaEntity sellerRejectionJpaEntity = new SellerRejectionJpaEntity();
            sellerRejectionJpaEntity.setSeller(sellerJpaEntity);
            sellerRejectionJpaEntity.setReasonsForRejections(List.of(body.reasonForRejection()));
            sellerRejectionJpaEntity.setNumberOfRejections(rejectionCount);
            sellerRejectionRepository.save(sellerRejectionJpaEntity);
        }

        HttpEntity<RejectSellerRequest> request = new HttpEntity<>(body, headers);
        ResponseEntity<ApiResponse> response = scenarioContext.getRestTemplate()
                .exchange(this.scenarioContext.url(path), HttpMethod.PATCH, request, ApiResponse.class);

        scenarioContext.setResponse(response);
    }


    @And("the seller rejection count should be {int}")
    public void theSellerRejectionCountShouldBe(int rejectionCount) {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<SellerRejectionJpaEntity> entityOpt = sellerRejectionRepository.findBySellerId(sellerId);
        Assertions.assertTrue(entityOpt.isPresent());
        SellerRejectionJpaEntity entity = entityOpt.get();
        Assertions.assertEquals(rejectionCount, entity.getNumberOfRejections());
    }

    //    SCENARIO:  Reject rejection if seller does not exist
    @Given("non existing seller and rejection count {int}")
    public void noSellerExistsIfSellerDoesExist(int rejectionCount) {
        scenarioContext.getHeadersMap().put("sellerId", UUID.randomUUID().toString());
        scenarioContext.getHeadersMap().put("rejectionCount", String.valueOf(rejectionCount));
    }
}