package com.biznopay.authservice.bdd.steps.user.Seller;

import com.biznopay.authservice.bdd._config.ScenarioContext;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.SellerRejectionJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ResubmitSellerSteps {
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

    @And("i am authenticated as the seller")
    public void iAmAuthenticatedAsTheSeller() {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> entityOpt = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(entityOpt.isPresent());
        UserJpaEntity entity = entityOpt.get();
        String token = jwtHelper.generate(entity.getPassword(), entity.getRole(), entity.getStatus().name(), entity.getEmail());
        scenarioContext.getHeadersMap().put("token", token);
    }

    @When("i send a PATCH multipart request to resubmit seller using endpoint {string}")
    public void iSendAPatchMultipartRequestToResubmitSellerUsingEndpoint(String path, DataTable dataTable) throws Exception {
        String token = scenarioContext.getHeadersMap().get("token");

        Map<String, String> data = dataTable.asMap(String.class, String.class);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        scenarioContext.getHeadersMap().put("lastData", scenarioContext.getObjectMapper().writeValueAsString(data));
        scenarioContext.getHeadersMap().put("lastPath", path);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(MediaType.APPLICATION_JSON);
        body.add("data", new HttpEntity<>(scenarioContext.getObjectMapper().writeValueAsString(data), dataHeaders));

        scenarioContext.getFileParts().forEach((field, filePart) -> {
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(filePart.contentType()));
            body.add(field, new HttpEntity<>(filePart.toResource(), fileHeaders));
        });

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<ApiResponse> response = scenarioContext.getRestTemplate()
                .exchange(scenarioContext.url(path), HttpMethod.PATCH, request, ApiResponse.class);

        scenarioContext.setResponse(response);
        scenarioContext.clearFileParts();
    }

    @And("the seller first name should be {string}")
    public void theSellerFirstNameShouldBe(String expectedFirstName) {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> user = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(expectedFirstName, user.get().getFirstName());
    }

    @And("the seller last name should be {string}")
    public void theSellerLastNameShouldBe(String expectedLastName) {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> user = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(expectedLastName, user.get().getLastName());
    }

    @And("the seller phone should be {string}")
    public void theSellerPhoneShouldBe(String expectedPhone) {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> user = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(expectedPhone, user.get().getPhone());
    }

    @And("the seller store name should be {string}")
    public void theSellerStoreNameShouldBe(String expectedStoreName) {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> user = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(user.isPresent());
        SellerJpaEntity seller = (SellerJpaEntity) user.get();
        Assertions.assertEquals(expectedStoreName, seller.getStoreName());
    }

    @And("the seller store description should be {string}")
    public void theSellerStoreDescriptionShouldBe(String expectedStoreDescription) {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> user = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(user.isPresent());
        SellerJpaEntity seller = (SellerJpaEntity) user.get();
        Assertions.assertEquals(expectedStoreDescription, seller.getStoreDescription());
    }

    @And("the seller nuit should be {string}")
    public void theSellerNuitShouldBe(String expectedNuit) {
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> user = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(user.isPresent());
        SellerJpaEntity seller = (SellerJpaEntity) user.get();
        Assertions.assertEquals(expectedNuit, seller.getNuit());
    }

    @And("with files for resubmit:")
    public void andWithFilesForResubmit(DataTable dataTable) throws IOException {
        String path = scenarioContext.getHeadersMap().get("lastPath");
        String token = scenarioContext.getHeadersMap().get("token");

        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String field = row.get("field");
            String filename = row.get("filename");
            String contentType = row.get("contentType");
            byte[] bytes = getClass().getClassLoader()
                    .getResourceAsStream("fixtures/images/" + filename)
                    .readAllBytes();
            scenarioContext.addFilePart(field, filename, contentType, bytes);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(MediaType.APPLICATION_JSON);
        String requestData = scenarioContext.getHeadersMap().get("lastData");
        body.add("data", new HttpEntity<>(requestData, dataHeaders));

        scenarioContext.getFileParts().forEach((field, filePart) -> {
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(filePart.contentType()));
            body.add(field, new HttpEntity<>(filePart.toResource(), fileHeaders));
        });

        //The last request was resubmitting that's why i'm setting the status to REJECTED
        UUID sellerId = UUID.fromString(scenarioContext.getHeadersMap().get("sellerId"));
        Optional<UserJpaEntity> entityOpt = userJpaRepository.findById(sellerId);
        Assertions.assertTrue(entityOpt.isPresent());
        UserJpaEntity entity = entityOpt.get();
        entity.setStatus(UserStatus.REJECTED);
        userJpaRepository.save(entity);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<ApiResponse> response = scenarioContext.getRestTemplate()
                .exchange(scenarioContext.url(path), HttpMethod.PATCH, request, ApiResponse.class);

        scenarioContext.setResponse(response);
        scenarioContext.clearFileParts();
    }
}