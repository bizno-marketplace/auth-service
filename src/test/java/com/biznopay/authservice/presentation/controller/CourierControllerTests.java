package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice._config.ContainerBase;
import com.biznopay.authservice._config.TestConfig;
import com.biznopay.authservice.domain.entity.user.Courier;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.CourierJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.*;
import com.biznopay.authservice.presentation.dto.RegisterCourierRequest;
import com.biznopay.authservice.usecase.sa.RegisterSAOutput;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

import static com.biznopay.authservice.testcases.CourierTestCases.*;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN_JPA;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CourierControllerTests extends ContainerBase {
    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private AddressJpaRepository addressJpaRepository;
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private MeterRegistry registry;
    @Autowired
    private SellerRejectionJpaRepository sellerRejectionJpaRepository;
    @Autowired
    private ActivationTokenJpaRepository activationTokenRepository;
    @Autowired
    private OutboxEventJpaRepository outboxEventJpaRepository;

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
        jdbcTemplate.execute("TRUNCATE TABLE t_addresses RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE T_ACTIVATION_TOKENS RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE T_OUTBOX_EVENTS RESTART IDENTITY CASCADE");
        Meter meter = registry.find("auth.seller.resubmitted").meter();
        if (meter != null) registry.remove(meter);
    }


    @Test
    @DisplayName("Should return 403 when user is not authenticated")
    public void shouldReturn403WhenUserIsNotAuthenticated() {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when token is invalid")
    public void shouldReturn403WhenTokenIsInvalid() {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer invalid_token");

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }


    @Test
    @DisplayName("Should return 403 if logged user is not supper admin")
    public void shouldReturn403IfLoggedUserIsNotSupperAdmin() {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        CourierJpaEntity entity = validCourierJpaEntity();
        userJpaRepository.save(entity);

        Courier courier = (Courier) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(courier);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("Access denied", response.getBody().error().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return 400 when firstName is empty or null")
    public void shouldReturn400WhenFirstNameIsNullOrEmpty(String firstName) {
        RegisterCourierRequest request = new RegisterCourierRequest(firstName, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(entity);

        SuperAdmin sa = (SuperAdmin) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(sa);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("First name is required", response.getBody().error().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return 400 when lastName is empty or null")
    public void shouldReturn400WhenLastNameIsNullOrEmpty(String lastName) {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, lastName, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(entity);

        SuperAdmin sa = (SuperAdmin) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(sa);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Last name is required", response.getBody().error().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return 400 when email is empty or null")
    public void shouldReturn400WhenEmailIsNullOrEmpty(String email) {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, email,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(entity);

        SuperAdmin sa = (SuperAdmin) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(sa);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("E-mail is required", response.getBody().error().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return 400 when phone is empty or null")
    public void shouldReturn400WhenPhoneIsNullOrEmpty(String phone) {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                phone, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(entity);

        SuperAdmin sa = (SuperAdmin) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(sa);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Phone is required", response.getBody().error().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return 400 when password is empty or null")
    public void shouldReturn400WhenPasswordIsNullOrEmpty(String password) {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, password, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(entity);

        SuperAdmin sa = (SuperAdmin) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(sa);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Password is required", response.getBody().error().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return 400 when license number is empty or null")
    public void shouldReturn400WhenLicenseNumberIsNullOrEmpty(String licenseNumber) {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, licenseNumber, VALID_ZONE);

        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(entity);

        SuperAdmin sa = (SuperAdmin) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(sa);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("License number is required", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should return 400 when vehicle type is empty or null")
    public void shouldReturn400WhenVehicleTypeIsNullOrEmpty() {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, null, VALID_LICENSE_NUMBER, VALID_ZONE);

        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(entity);

        SuperAdmin sa = (SuperAdmin) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(sa);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Vehicle type is required", response.getBody().error().message());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should return 400 when vehicle type is empty or null")
    public void shouldReturn400WhenZoneIsNullOrEmpty(String zone) {
        RegisterCourierRequest request = new RegisterCourierRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL,
                VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, zone);

        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(entity);

        SuperAdmin sa = (SuperAdmin) UserMapper.toUserDomain(entity);
        String token = jwtHelper.generate(sa);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<RegisterSAOutput>> response = restTemplate.exchange(
                url("/couriers/register"),
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSAOutput>>() {
                }
        );
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Zone is required", response.getBody().error().message());
    }
}
