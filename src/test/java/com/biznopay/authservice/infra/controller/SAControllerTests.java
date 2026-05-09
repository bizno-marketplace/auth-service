package com.biznopay.authservice.infra.controller;


import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@Transactional
@ActiveProfiles("test")
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SAControllerTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer(
            DockerImageName.parse("postgres:latest")
    );

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private UserJpaRepository userRepository;

    @BeforeEach
    void setUp() {
        restTemplate = new TestRestTemplate();
        userRepository.deleteAll();
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @EmptySource
    @ParameterizedTest
    @DisplayName("Should return 400 if first name is empty")
    void shouldReturn400IfFirstNameIsEmpty(String firstName) {
        RegisterSARequest request = new RegisterSARequest(firstName, "Smith", "johnsmith@bizno.co.mz", "Password@123");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("First name is required", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 422 if first name is invalid")
    void shouldReturn422IfFirstNameIsInvalid() {
        RegisterSARequest request = new RegisterSARequest("Jo", "Smith", "johnsmith@bizno.co.mz", "Password@123");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        Assertions.assertEquals("First name must be at least 3 characters long", response.getBody().error().message());
    }

    @EmptySource
    @ParameterizedTest
    @DisplayName("Should return 400 if last name is empty")
    void shouldReturn400IfLastNameIsEmpty(String lastname) {
        RegisterSARequest request = new RegisterSARequest("John", lastname, "johnsmith@bizno.co.mz", "Password@123");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Last name is required", response.getBody().error().message());
    }

}
