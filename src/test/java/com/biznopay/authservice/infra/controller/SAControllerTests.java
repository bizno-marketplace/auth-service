package com.biznopay.authservice.infra.controller;


import com.biznopay.authservice.config.PostgresContainerBase;
import com.biznopay.authservice.config.TestConfig;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.mocks.Mocks;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SAControllerTests extends PostgresContainerBase {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @AfterAll
    static void tearDown() {
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }

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
        FuncUtils funcUtils = new FuncUtils();
        RegisterSARequest request = Mocks.registerSARequestMock();
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 409 when Super admin already exists")
    public void shouldReturn409WhenSuperAdminAlreadyExists() {
        RegisterSARequest request = Mocks.registerSARequestMock();
        UserJpaEntity entity = Mocks.supperAdminJpaEntityMockFromSuperAdmin();
        userJpaRepository.save(entity);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertEquals("Super admin already exists", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 409 if email is already in use")
    public void shouldReturn409IfEmailIsAlreadyInUse() {
        RegisterSARequest request = Mocks.registerSARequestMock();
        UserJpaEntity entity = Mocks.buyerJpaEntityMockFromBuyer();
        userJpaRepository.save(entity);
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertEquals("Email already in use", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 400 if first name is empty")
    void shouldReturn400IfFirstNameIsEmpty() {
        RegisterSARequest request = Mocks.registerSARequestEmptyFieldMock("firstname");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("First name is required", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 422 if first name is invalid")
    void shouldReturn422IfFirstNameIsInvalid() {
        RegisterSARequest request = Mocks.registerSARequestInvalidFieldMock("firstname");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        Assertions.assertEquals("First name must be at least 3 characters long", response.getBody().error().message());
    }

    @EmptySource
    @ParameterizedTest
    @DisplayName("Should return 400 if last name is empty")
    void shouldReturn400IfLastNameIsEmpty(String lastname) {
        RegisterSARequest request = Mocks.registerSARequestEmptyFieldMock("lastname");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Last name is required", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 422 if last name is invalid")
    void shouldReturn422IfLastNameIsInvalid() {
        RegisterSARequest request = Mocks.registerSARequestInvalidFieldMock("lastname");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        Assertions.assertEquals("Last name must be at least 3 characters long", response.getBody().error().message());
    }

    @EmptySource
    @ParameterizedTest
    @DisplayName("Should return 400 if email is empty")
    void shouldReturn400IfEmailIsEmpty(String email) {
        RegisterSARequest request = Mocks.registerSARequestEmptyFieldMock("email");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("E-mail is required", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 422 if email is invalid")
    void shouldReturn400IfEmailIsInvalid() {
        RegisterSARequest request = Mocks.registerSARequestInvalidFieldMock("email");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        Assertions.assertEquals("E-mail must be a bizno institutional email", response.getBody().error().message());
    }

    @EmptySource
    @ParameterizedTest
    @DisplayName("Should return 400 if password is empty")
    void shouldReturn400IfPasswordIsEmpty(String password) {
        RegisterSARequest request = Mocks.registerSARequestEmptyFieldMock("password");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Password is required", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 422 if password is invalid")
    void shouldReturn400IfPasswordIsInvalid() {
        RegisterSARequest request = Mocks.registerSARequestInvalidFieldMock("password");
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url("/supper-admins"), request, ApiResponse.class);
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        Assertions.assertEquals("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character", response.getBody().error().message());
    }
}
