package com.biznopay.authservice.bdd.steps;

import com.biznopay.authservice.bdd.ScenarioContext;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.mocks.Mocks;
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
import java.util.Optional;

public class RegisterBuyerSteps {

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
        scenarioContext.setJdbcTemplate(jdbcTemplate);
        jdbcTemplate.execute("TRUNCATE TABLE t_activation_tokens RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE t_users RESTART IDENTITY CASCADE");
    }

    private String email;

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
        Assertions.assertEquals(user.get().getCreatedAt().plusDays(2), user.get().getExpiresAt());
    }

    // SCENARIO: Attempt to register with an already registered email
    @Given("a buyer already exists with email {string}")
    public void aBuyerExistsWithEmail(String email) {
        UserJpaEntity entity = Mocks.buyerJpaEntityMock();
        entity.setEmail(email);
        userJpaRepository.save(entity);

        this.email = email;
        Optional<UserJpaEntity> user = userJpaRepository.findByEmail(email);
        Assertions.assertTrue(user.isPresent());
    }
}