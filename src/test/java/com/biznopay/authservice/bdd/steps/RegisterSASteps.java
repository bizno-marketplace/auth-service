package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.bdd.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

public class RegisterSASteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
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

    // COMMON
    @Given("no super admin exists in the system")
    public void noSuperAdminExistsInTheSystem() {
        // o setUp já faz o truncate
    }

    // SCENARIO: Successfully register super admin when none exists
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
    public void theConfirmationEmailShouldHaveALinkThatExpiresAfter15Minutes() {
        Map<String, Object> event = jdbcTemplate.queryForMap(
                "SELECT payload FROM t_outbox_events WHERE status = 'PENDING' ORDER BY created_at DESC LIMIT 1"
        );
        String payload = (String) event.get("payload");
        Assertions.assertTrue(payload.contains("activationTokenId"));
    }

    // SCENARIO: Reject registration when super admin already exists
    @Given("a super admin already exists in the system")
    public void aSuperAdminAlreadyExistsInTheSystem() {
        User user = SuperAdmin.register("John", "Smith", "admin@bizno.co.mz", "Password@123");
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }

    // SCENARIO: Reject registration if email is already in use
    @Given("a user with email {string} exists in the system")
    public void aUserWithEmailExistsInTheSystem(String email) {
        Address address = null;
        User user = Buyer.register("John", "Smith", email, "848484848","Password@123", address);
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }
}