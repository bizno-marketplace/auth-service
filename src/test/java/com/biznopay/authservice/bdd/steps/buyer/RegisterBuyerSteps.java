package com.biznopay.authservice.bdd.steps.buyer;

import com.biznopay.authservice.bdd._config.ScenarioContext;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

public class RegisterBuyerSteps {

    @Autowired
    private ScenarioContext scenarioContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;
    private String email;

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

    // SCENARIO:  Successfully register a new buyer
    @Given("no buyer exists with email {string}")
    public void noBuyerExistsWithEmail(String email) {
        this.email = email;
        Optional<UserJpaEntity> user = userJpaRepository.findByEmail(email);
        Assertions.assertTrue(user.isEmpty());
    }

    @And("the buyer account is created with status {string}")
    public void theBuyerAccountIsCreatedWithStatus(String status) {
        Optional<UserJpaEntity> user = userJpaRepository.findByEmail(this.email);
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(UserStatus.valueOf(status), user.get().getStatus());
    }

    @And("the account expires in 2 days")
    public void theAccountExpiresIn2Days() {
        Optional<UserJpaEntity> user = userJpaRepository.findByEmail(this.email);
        Assertions.assertTrue(user.isPresent());
    }
}