package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.config.ContainerBase;
import com.biznopay.authservice.config.TestConfig;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.RegisterSARequest;
import com.biznopay.authservice.usecase.sa.RegisterSAOutput;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN_JPA;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SAControllerTests extends ContainerBase {

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

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.SuperAdminTestCases#registerSControllerCases")
    void shouldHandleRegistration(
            String testName,
            RegisterSARequest request,
            HttpStatus expectedStatus,
            String expectedMessage
    ) {
        if (testName.equals("Conflict SA exists")) {
            userJpaRepository.save(VALID_SUPER_ADMIN_JPA);
        }

        if (testName.equals("Success")) {
            ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                    url("/supper-admins/register"),
                    HttpMethod.POST,
                    new HttpEntity<>(request),
                    new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                    }
            );
            Assertions.assertEquals(expectedStatus, response.getStatusCode());
            Assertions.assertEquals(expectedMessage, response.getBody().data().message());
        } else {
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    url("/supper-admins/register"),
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