package com.biznopay.authservice.bdd.steps.user.Sellers;

import com.biznopay.authservice.bdd._config.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.AddressRequest;
import com.biznopay.authservice.presentation.dto.RegisterSellerRequest;
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
import org.springframework.http.MediaType;
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

import static com.biznopay.authservice.testcases.SellerTestCases.*;

public class RegisterSellerSteps {
    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;
    private String path;

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

    //    SCENARIO: Successfully register a new seller
    @Given("no seller exists with email {string}")
    public void noSellerExistsWithEmail(String email) {
        Optional<UserJpaEntity> user = userJpaRepository.findByEmail(email);
        Assertions.assertTrue(user.isEmpty());
    }

    @When("i send a multipart POST request to {string} with data:")
    public void iSendMultiPartPostRequestToWithData(String path, DataTable dataTable) {
        this.path = path;
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        AddressRequest addressRequest = new AddressRequest(
                data.get("latitude") != null && !data.get("latitude").isBlank() ? Double.parseDouble(data.get("latitude")) : null,
                data.get("longitude") != null && !data.get("longitude").isBlank() ? Double.parseDouble(data.get("longitude")) : null,
                data.get("street"),
                data.get("neighbourhood"),
                data.get("city"),
                data.get("province"),
                data.get("country")
        );

        String firstname = data.get("firstName");
        String lastName = data.get("lastName");
        String email = data.get("email");
        String phoneNumber = data.get("phoneNumber");
        String password = data.get("password");
        String storeName = data.get("storeName");
        String storeDescription = data.get("storeDescription");
        String nuit = data.get("nuit");
        RegisterSellerRequest request = new RegisterSellerRequest(firstname, lastName, email, phoneNumber, password, storeName, storeDescription, nuit, addressRequest);
        scenarioContext.setRequestData(request);
    }

    @And("with files:")
    public void andWithFiles(DataTable dataTable) throws IOException {
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

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("data", scenarioContext.getRequestData());

        scenarioContext.getFileParts().forEach((field, part) -> {
            HttpHeaders fileHeaders = new HttpHeaders();
            fileHeaders.setContentType(MediaType.parseMediaType(part.contentType()));
            body.add(field, new HttpEntity<>(part.toResource(), fileHeaders));
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        scenarioContext.setResponse(scenarioContext.getRestTemplate().postForEntity(scenarioContext.url(path),
                new HttpEntity<>(body, headers), ApiResponse.class)
        );
    }

    @Given("a seller already exists with nuit {string}")
    public void aUserWithEmailExistsInTheSystem(String nuit) {
        User user = registerSeller(
                VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,
                VALID_STORE_NAME, VALID_STORE_DESC, nuit, VALID_ADDRESS_NEW, VALID_BI);
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }
}
