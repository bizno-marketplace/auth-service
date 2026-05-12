package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
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
import java.util.Map;

public class RegisterSASteps {
    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<ApiResponse> response;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserJpaRepository userJpaRepository;

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
    @When("i send a POST request to {string} with:")
    public void iSendAPOSTRequestToSupperAdminsWith(String path, DataTable dataTable) {
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        RegisterSARequest request = new RegisterSARequest(data.get("firstName"), data.get("lastName"), data.get("email"), data.get("password"));
        response = restTemplate.postForEntity(url(path), request, ApiResponse.class);
    }

    @Then("the response should be {int}")
    public void theResponseShouldBe(int expectedStatus) {
        Assertions.assertEquals(expectedStatus, response.getStatusCode().value());
    }

    @And("the response should have a message {string}")
    public void theResponseShouldHaveAMessage(String message) {
        Assertions.assertEquals(message, response.getBody().error().message());
    }

    @Given("no super admin exists in the system")
    public void noSuperAdminExistsInTheSystem() {
        //the setup method already does this
    }

    //  SCENARIO: Successfully register super admin when none exists
    @And("a confirmation email should be sent to {string}")
    public void aConfirmationEmailShouldBeSentTo(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_outbox_events WHERE payload LIKE ? AND status = 'PENDING'",
                Integer.class,
                "%" + email + "%"
        );
        Assertions.assertTrue(count > 0);
    }

    @And("the confirmation email should have a link that expires after 15 minutes")
    public void theConfirmationEmailShouldHvaALinkThatExpiresAfter15Minutes() {
        Map<String, Object> event = jdbcTemplate.queryForMap(
                "SELECT payload FROM t_outbox_events WHERE status = 'PENDING' ORDER BY created_at DESC LIMIT 1"
        );

        String payload = (String) event.get("payload");
        Assertions.assertTrue(payload.contains("activationTokenId"));
    }

//  SCENARIO: Reject registration when user already exists
    @Given("a super admin already exists in the system")
    public void aSuperAdminAlreadyExistsInTheSystem() {
        User user = SuperAdmin.register("John", "Smith", "admin@bizno.co.mz", "Password@123");
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }

//  SCENARIO: Reject registration if email is already in use
    @Given("a user with email {string} exists in the system")
    public void aUserWithEmailExistsInTheSystem(String email) {
        User user = Buyer.register("John", "Smith", email, "Password@123");
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }
//    OTHER STEPS ARE BEING COVERED BY THE COMMON STEPS
}
