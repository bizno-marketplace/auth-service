package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice._config.ContainerBase;
import com.biznopay.authservice._config.TestConfig;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.RegisterBuyerRequest;
import com.biznopay.authservice.usecase.buyer.RegisterBuyerOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BuyerControllerTests extends ContainerBase {
    @LocalServerPort
    private int port;

    private RestTemplate  restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;


    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.BuyerTestCases#invalidControllerRegistrationCases")
    void shouldRejectInvalidRegistration(
            String testName,
            RegisterBuyerRequest request,
            HttpStatus expectedStatus,
            String expectedMessage
    ) {
        if (testName.equals("Conflict")) {
            restTemplate.exchange(
                    url("/buyers/register"),
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponse<Void>>() {
                    }
            );
        }

        if (testName.equals("Success")) {
            ResponseEntity<ApiResponse<RegisterBuyerOutput>> response = restTemplate.exchange(
                    url("/buyers/register"),
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponse<RegisterBuyerOutput>>() {
                    }
            );

            Assertions.assertEquals(expectedStatus, response.getStatusCode());
            Assertions.assertEquals(expectedMessage, response.getBody().data().message());
        } else {
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    url("/buyers/register"),
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponse<Void>>() {
                    }
            );
            Assertions.assertEquals(expectedStatus, response.getStatusCode());
            Assertions.assertEquals(expectedMessage, response.getBody().error().message());
        }
    }
}
