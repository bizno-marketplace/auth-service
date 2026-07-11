package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice._config.ContainerBase;
import com.biznopay.authservice._config.TestConfig;
import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.util.DocumentPathGenerator;
import com.biznopay.authservice.domain.util.DomainFuncUtils;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.AddressJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.BuyerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.AddressJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import com.biznopay.authservice.presentation.dto.RegisterSellerRequest;
import com.biznopay.authservice.usecase.seller.register.RegisterSellerOutput;
import com.biznopay.authservice.utils.NamedByteArrayResource;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.BuyerTestCases.VALID_BUYER_JPA;
import static com.biznopay.authservice.testcases.SellerTestCases.*;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN_JPA;

@Tag("integration")
@ActiveProfiles("test")
@Import({TestConfig.class})
@AutoConfigureRestTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SellerControllerTests extends ContainerBase {
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
                url("/sellers/register"),
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
                url("/sellers/register"),
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
            User user = validSellerWithNotSavedAddress("test@email.com");
            UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
            userJpaRepository.save(entity);
        }

        if (testName.equals("E-mail conflict")) {
            User user = validSellerWithNotSavedAddress();
            UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
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
                url("/sellers/register"),
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

    //Approve seller
    @Test
    @DisplayName("Should throw AccessDeniedException if no auth token is provided")
    public void shouldThrowAccessDeniedExceptionIfNoAuthTokenIsProvided() {
        String userId = UUID.randomUUID().toString();
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/"+userId+"/approve"),
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if logged user is not supper admin")
    public void shouldThrowAccessDeniedExceptionIfLoggedUserIsNotSupperAdmin(){
        SellerJpaEntity entity = (SellerJpaEntity)  VALID_SELLER_JPA;
        String userId =  entity.getId().toString();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/"+userId+"/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("Access denied", response.getBody().error().message());

    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if seller does not exists")
    public void shouldThrowResourceNotFoundExceptionIfSellerDoesNotExists(){
        BuyerJpaEntity entity = (BuyerJpaEntity)  VALID_BUYER_JPA;
        String sellerId =  entity.getId().toString();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setDeliveryAddresses(List.of(addressJpaEntity));
        userJpaRepository.save(entity);

        UserJpaEntity sa = VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(sa);
        String token = jwtHelper.generate(sa.getId().toString(), sa.getRole(), sa.getStatus().name(), sa.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/"+sellerId+"/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Seller not found", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should throw InvalidSellerAccountStatus if seller account is not in AWAITING_APPROVAL status")
    public void shouldThrowInvalidSellerAccountStatusIfSellerAccountIsNotInAwaitingApprovalStatus(){
        SellerJpaEntity entity = (SellerJpaEntity)  VALID_SELLER_JPA;
        String sellerId =  entity.getId().toString();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        entity.setStatus(UserStatus.PENDING);
        userJpaRepository.save(entity);

        UserJpaEntity sa = VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(sa);
        String token = jwtHelper.generate(sa.getId().toString(), sa.getRole(), sa.getStatus().name(), sa.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/"+sellerId+"/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Can only perform this action to Sellers with status AWAITING_APPROVAL", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should active user and increment seller approved metrics")
    public void shouldActiveUserAndIncrementSellerApprovedMetrics(){
        SellerJpaEntity entity = (SellerJpaEntity)  VALID_SELLER_JPA;
        String sellerId =  entity.getId().toString();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        entity.setStatus(UserStatus.AWAITING_APPROVAL);
        userJpaRepository.save(entity);

        UserJpaEntity sa = VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(sa);
        String token = jwtHelper.generate(sa.getId().toString(), sa.getRole(), sa.getStatus().name(), sa.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/"+sellerId+"/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {}
        );

        Optional<UserJpaEntity> resultOpt = userJpaRepository.findById(entity.getId());
        double valor = registry.get("auth.seller.approved").counter().count();
        Assertions.assertTrue(resultOpt.isPresent());
        Assertions.assertEquals(1, valor);
        UserJpaEntity result = resultOpt.get();
        Assertions.assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

}
