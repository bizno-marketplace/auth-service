package com.biznopay.authservice.infra.controller;

import com.biznopay.authservice.config.ContainerBase;
import com.biznopay.authservice.config.TestConfig;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.RegisterBuyerRequest;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.mocks.Mocks;
import com.biznopay.authservice.usecase.user.register.buyer.RegisterBuyerOutput;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BuyerControllerTests extends ContainerBase {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
        jdbcTemplate.execute("TRUNCATE TABLE t_users RESTART IDENTITY CASCADE");
    }

    @Test
    @DisplayName("Should return 200 on successfully registration")
    void shouldReturn200OnSuccessfullyRegistration() {
        RegisterBuyerRequest request = Mocks.registerBuyerRequestMock();
        ResponseEntity<ApiResponse<RegisterBuyerOutput>> response = restTemplate.exchange(
                url("/buyers"),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<RegisterBuyerOutput>>() {}
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        RegisterBuyerOutput output = response.getBody().data();
        Assertions.assertEquals("We've sent an activation link to provided email: " + request.email(), output.message());
    }
}
