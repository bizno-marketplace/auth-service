package com.biznopay.authservice.infra.controller;

import com.biznopay.authservice.config.ContainerBase;
import com.biznopay.authservice.config.TestConfig;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.dto.RegisterBuyerRequest;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.mocks.Mocks;
import com.biznopay.authservice.usecase.user.register.buyer.RegisterBuyerOutput;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

import java.util.stream.Stream;

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

    static Stream<Arguments> invalidRegistrationCases() {
        return Stream.of(
                Arguments.of("First name is required",
                        new RegisterBuyerRequest("", "Machava", "ana.machava@gmail.com", "Segura@123", "+258841234567", Mocks.validAddressRequestMock()),
                        HttpStatus.BAD_REQUEST, "First name is required"),

                Arguments.of("Last name is required",
                        new RegisterBuyerRequest("Ana", "", "ana.machava@gmail.com", "Segura@123", "+258841234567", Mocks.validAddressRequestMock()),
                        HttpStatus.BAD_REQUEST, "Last name is required"),

                Arguments.of("Email is required",
                        new RegisterBuyerRequest("Ana", "Machava", "", "Segura@123", "+258841234567", Mocks.validAddressRequestMock()),
                        HttpStatus.BAD_REQUEST, "E-mail is required"),

                Arguments.of("Invalid email",
                        new RegisterBuyerRequest("Ana", "Machava", "not-an-email", "Segura@123", "+258841234567", Mocks.validAddressRequestMock()),
                        HttpStatus.BAD_REQUEST, "Invalid E-mail"),

                Arguments.of("Password is required",
                        new RegisterBuyerRequest("Ana", "Machava", "ana.machava@gmail.com", "", "+258841234567", Mocks.validAddressRequestMock()),
                        HttpStatus.BAD_REQUEST, "Password is required"),

                Arguments.of("Phone is required",
                        new RegisterBuyerRequest("Ana", "Machava", "ana.machava@gmail.com", "Segura@123", "", Mocks.validAddressRequestMock()),
                        HttpStatus.BAD_REQUEST, "Phone number is required"),

                Arguments.of("Password must be at least 8 characters",
                        new RegisterBuyerRequest("Ana", "Machava", "ana.machava@gmail.com", "abc123", "+258841234567", Mocks.validAddressRequestMock()),
                        HttpStatus.UNPROCESSABLE_CONTENT, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"),

                Arguments.of("Latitude is required",
                        new RegisterBuyerRequest("Ana", "Machava", "ana.machava@gmail.com", "Segura@123", "+258841234567", Mocks.addressRequestWithLatitude(null)),
                        HttpStatus.BAD_REQUEST, "Latitude is required"),

                Arguments.of("Longitude is required",
                        new RegisterBuyerRequest("Ana", "Machava", "ana.machava@gmail.com", "Segura@123", "+258841234567", Mocks.addressRequestWithLongitude(null)),
                        HttpStatus.BAD_REQUEST, "Longitude is required"),

                Arguments.of("Invalid latitude value",
                        new RegisterBuyerRequest("Ana", "Machava", "ana.machava@gmail.com", "Segura@123", "+258841234567", Mocks.addressRequestWithLatitude(-999.0)),
                        HttpStatus.UNPROCESSABLE_CONTENT, "Invalid Latitude on Address"),

                Arguments.of("Invalid longitude value",
                        new RegisterBuyerRequest("Ana", "Machava", "ana.machava@gmail.com", "Segura@123", "+258841234567", Mocks.addressRequestWithLongitude(999.0)),
                        HttpStatus.UNPROCESSABLE_CONTENT, "Invalid Longitude on Address")
        );
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
        RegisterBuyerRequest request = Mocks.registerBuyerRequestMock();
        ResponseEntity<ApiResponse<RegisterBuyerOutput>> response = restTemplate.exchange(
                url("/buyers"),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<RegisterBuyerOutput>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        RegisterBuyerOutput output = response.getBody().data();
        Assertions.assertEquals("We've sent an activation link to provided email: " + request.email(), output.message());
    }

    @Test
    @DisplayName("Should return 409 when email is already in use")
    public void shouldReturn409WhenEmailIsAlreadyInUse() {
        RegisterBuyerRequest request = Mocks.registerBuyerRequestMock();
        UserJpaEntity entity = Mocks.buyerJpaEntityMock();
        entity.setEmail(request.email());
        userJpaRepository.save(entity);

        ResponseEntity<ApiResponse<RegisterBuyerOutput>> response = restTemplate.exchange(
                url("/buyers"),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<RegisterBuyerOutput>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        Assertions.assertEquals("Email already in use", response.getBody().error().message());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRegistrationCases")
    @DisplayName("Should reject registration with invalid or missing fields")
    void shouldRejectInvalidRegistration(
            String testName,
            RegisterBuyerRequest request,
            HttpStatus expectedStatus,
            String expectedError
    ) {
        ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                url("/buyers"),
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<ApiResponse<Void>>() {
                }
        );

        Assertions.assertEquals(expectedStatus, response.getStatusCode());
        Assertions.assertEquals(expectedError, response.getBody().error().message());
    }
}
