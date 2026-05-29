package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.config.ContainerBase;
import com.biznopay.authservice.config.TestConfig;
import com.biznopay.authservice.domain.entity.user.Seller;
import com.biznopay.authservice.domain.util.DocumentPathGenerator;
import com.biznopay.authservice.domain.util.DomainFuncUtils;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.RegisterSellerRequest;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSellerOutput;
import com.biznopay.authservice.utils.NamedByteArrayResource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

import static com.biznopay.authservice.testcases.SellerTestCases.*;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SellerControllerTests extends ContainerBase {
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
    @DisplayName("Should return 400 when front bi photo is null")
    void shouldReturn400WhenFrontBiPhotoIsNull() throws IOException {
        String backImageName = "bi_verso.png";

        byte[] biBackBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + backImageName)
                .readAllBytes();


        RegisterSellerRequest request = new RegisterSellerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS_REQUEST);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        body.add("data", new HttpEntity<>(request, dataHeaders));

        HttpHeaders backHeaders = new HttpHeaders();
        backHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);
        body.add("biBackPhoto", new HttpEntity<>(new NamedByteArrayResource(biBackBytes, backImageName), backHeaders));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ApiResponse<RegisterSellerOutput>> response = restTemplate.exchange(
                url("/sellers"),
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSellerOutput>>() {
                }

        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("BI front photo is required", response.getBody().error().message());

    }

    @Test
    @DisplayName("Should return 400 when back bi photo is null")
    void shouldReturn400WhenBackBiPhotoIsNull() throws IOException {
        String frontImageName = "bi_frente.png";

        byte[] biFrontBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + frontImageName)
                .readAllBytes();


        RegisterSellerRequest request = new RegisterSellerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS_REQUEST);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        body.add("data", new HttpEntity<>(request, dataHeaders));

        HttpHeaders frontHeaders = new HttpHeaders();
        frontHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);
        body.add("biFrontPhoto", new HttpEntity<>(new NamedByteArrayResource(biFrontBytes, frontImageName), frontHeaders));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ApiResponse<RegisterSellerOutput>> response = restTemplate.exchange(
                url("/sellers"),
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSellerOutput>>() {
                }

        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("BI back photo is required", response.getBody().error().message());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.SellerTestCases#controllerRegisterSellerCases")
    void shouldTestAllCases(
            String testName,
            RegisterSellerRequest request,
            String frontImageName,
            String backImageName,
            Class<? extends RuntimeException> expectedException,
            HttpStatus expectedStatus,
            String expectedMessage
    ) throws IOException {
        DomainFuncUtils domainFuncUtils = new DomainFuncUtils(); //just to cover
        DocumentPathGenerator documentPathGenerator = new DocumentPathGenerator(); //just to cover

        byte[] biFrontBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + frontImageName)
                .readAllBytes();

        byte[] biBackBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + backImageName)
                .readAllBytes();

        if (testName.equals("Nuit conflict")) {
            Seller seller = registerSeller(VALID_FIRST_NAME, VALID_LAST_NAME, "test@email.com", VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                    VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI);
            UserJpaEntity entity = UserMapper.toUserJpaEntity(seller);
            userJpaRepository.save(entity);
        }

        if (testName.equals("E-mail conflict")) {
            Seller seller = registerSeller(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                    VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI);
            UserJpaEntity entity = UserMapper.toUserJpaEntity(seller);
            userJpaRepository.save(entity);
        }


        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        body.add("data", new HttpEntity<>(request, dataHeaders));

        HttpHeaders frontHeaders = new HttpHeaders();
        frontHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);
        body.add("biFrontPhoto", new HttpEntity<>(new NamedByteArrayResource(biFrontBytes, frontImageName), frontHeaders));

        HttpHeaders backHeaders = new HttpHeaders();
        backHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);
        body.add("biBackPhoto", new HttpEntity<>(new NamedByteArrayResource(biBackBytes, backImageName), backHeaders));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ApiResponse<RegisterSellerOutput>> response = restTemplate.exchange(
                url("/sellers"),
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<RegisterSellerOutput>>() {
                }

        );

        Assertions.assertEquals(expectedStatus, response.getStatusCode());
        if (testName.equals("Success")) {
            RegisterSellerOutput output = response.getBody().data();
            Assertions.assertEquals("We've sent an activation link to provided email: " + request.email(), output.message());
        } else {
            Assertions.assertEquals(expectedMessage, response.getBody().error().message());
        }
    }

}
