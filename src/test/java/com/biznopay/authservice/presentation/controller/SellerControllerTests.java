package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice._config.ContainerBase;
import com.biznopay.authservice._config.TestConfig;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.util.DocumentPathGenerator;
import com.biznopay.authservice.domain.util.DomainFuncUtils;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.*;
import com.biznopay.authservice.infra.persistence.jpa.repository.*;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.presentation.dto.RegisterSellerRequest;
import com.biznopay.authservice.presentation.dto.ResubmitSellerRequest;
import com.biznopay.authservice.presentation.dto.UpdateSellerRequest;
import com.biznopay.authservice.usecase.seller.register.RegisterSellerOutput;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSellerOutput;
import com.biznopay.authservice.usecase.seller.updateSeller.UpdateSellerOutput;
import com.biznopay.authservice.utils.NamedByteArrayResource;
import io.micrometer.core.instrument.Meter;
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
import org.springframework.http.MediaType;
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
import java.util.*;

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
    @DisplayName("Should throw AccessDeniedException if no auth token is provided on approve seller")
    public void shouldThrowAccessDeniedExceptionIfNoAuthTokenIsProvidedOnApproveSeller() {
        String userId = UUID.randomUUID().toString();
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + userId + "/approve"),
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if logged user is not supper admin on approve seller")
    public void shouldThrowAccessDeniedExceptionIfLoggedUserIsNotSupperAdminOnApproveSeller() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String userId = entity.getId().toString();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + userId + "/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("Access denied", response.getBody().error().message());

    }

    @Test
    @DisplayName("Should throw InvalidFieldException if seller id is invalid on approve seller")
    public void shouldThrowInvalidFieldExceptionIfSellerIdIsInvalidOnApproveSeller() {
        String sellerId = "any_invalid_user_id";

        SuperAdminJpaEntity sa = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(sa);
        String token = jwtHelper.generate(sa.getId().toString(), sa.getRole(), sa.getStatus().name(), sa.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + sellerId + "/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        Assertions.assertEquals("Invalid User Id on APPROVE_SELLER", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if seller does not exists on approve seller")
    public void shouldThrowResourceNotFoundExceptionIfSellerDoesNotExistsApproveSeller() {
        BuyerJpaEntity entity = (BuyerJpaEntity) VALID_BUYER_JPA();
        String sellerId = entity.getId().toString();
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
                url("/sellers/" + sellerId + "/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Seller not found", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should throw InvalidSellerAccountStatus if seller account is not in AWAITING_APPROVAL status on approve seller")
    public void shouldThrowInvalidSellerAccountStatusIfSellerAccountIsNotInAwaitingApprovalStatusOnApproveSeller() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String sellerId = entity.getId().toString();
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
                url("/sellers/" + sellerId + "/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Can only perform this action to Sellers with status AWAITING_APPROVAL", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should active user and increment seller approved metrics")
    public void shouldActiveUserAndIncrementSellerApprovedMetrics() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String sellerId = entity.getId().toString();
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
                url("/sellers/" + sellerId + "/approve"),
                HttpMethod.PATCH,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Optional<UserJpaEntity> resultOpt = userJpaRepository.findById(entity.getId());
        double valor = registry.get("auth.seller.approved").counter().count();
        Assertions.assertTrue(resultOpt.isPresent());
        Assertions.assertEquals(1, valor);
        UserJpaEntity result = resultOpt.get();
        Assertions.assertEquals(UserStatus.ACTIVE, result.getStatus());
    }

    //Reject seller
    @Test
    @DisplayName("Should throw AccessDeniedException if no auth token is provided on reject seller")
    public void shouldThrowAccessDeniedExceptionIfNoAuthTokenIsProvidedOnApproveSellerOnRejectSeller() {
        String userId = UUID.randomUUID().toString();
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + userId + "/reject"),
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if logged user is not supper admin on reject seller")
    public void shouldThrowAccessDeniedExceptionIfLoggedUserIsNotSupperAdminOnRejectSeller() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String userId = entity.getId().toString();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("reasonForRejection", "any reason");

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + userId + "/reject"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("Access denied", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should throw InvalidFieldException if seller id is invalid on reject seller")
    public void shouldThrowInvalidFieldExceptionIfSellerIdIsInvalidOnRejectSeller() {
        String sellerId = "any_invalid_user_id";

        SuperAdminJpaEntity sa = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(sa);
        String token = jwtHelper.generate(sa.getId().toString(), sa.getRole(), sa.getStatus().name(), sa.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("reasonForRejection", "any reason");

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + sellerId + "/reject"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, response.getStatusCode());
        Assertions.assertEquals("Invalid User Id on REJECT_SELLER", response.getBody().error().message());
    }

    @Test
    @DisplayName("should throw RequiredFieldException if reason for rejection is missing on reject seller")
    public void shouldThrowRequiredFieldExceptionIfReasonForRejectionIsMissingOnRejectSeller() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String sellerId = entity.getId().toString();
        AddressJpaEntity addressJpa = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        entity.setStoreAddress(addressJpa);
        userJpaRepository.save(entity);

        SuperAdminJpaEntity sa = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(sa);
        String token = jwtHelper.generate(sa.getId().toString(), sa.getRole(), sa.getStatus().name(), sa.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("reasonForRejection", "");

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + sellerId + "/reject"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Reason for rejection is required", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if seller does not exists on reject seller")
    public void shouldThrowResourceNotFoundExceptionIfSellerDoesNotExistsOnRejectSeller() {
        BuyerJpaEntity entity = (BuyerJpaEntity) VALID_BUYER_JPA();
        ;
        String sellerId = entity.getId().toString();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setDeliveryAddresses(List.of(addressJpaEntity));
        userJpaRepository.save(entity);

        UserJpaEntity sa = VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(sa);
        String token = jwtHelper.generate(sa.getId().toString(), sa.getRole(), sa.getStatus().name(), sa.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("reasonForRejection", "any reason");

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + sellerId + "/reject"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Seller not found", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should throw InvalidSellerAccountStatus if seller account is not in AWAITING_APPROVAL status on reject seller")
    public void shouldThrowInvalidSellerAccountStatusIfSellerAccountIsNotInAwaitingApprovalStatusOnRejectSeller() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String sellerId = entity.getId().toString();
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
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("reasonForRejection", "any reason");

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + sellerId + "/reject"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Can only perform this action to Sellers with status AWAITING_APPROVAL", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should reject user and increment seller reject metrics")
    public void shouldBlockUserAndIncrementSellerRejectMetrics() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String sellerId = entity.getId().toString();
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
        headers.setContentType(MediaType.APPLICATION_JSON);

        String reasonForRejection = "any_reason";
        Map<String, Object> body = new HashMap<>();
        body.put("reasonForRejection", reasonForRejection);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + sellerId + "/reject"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Optional<UserJpaEntity> resultOpt = userJpaRepository.findById(entity.getId());
        double valor = registry.get("auth.seller.rejected").tag("reason", reasonForRejection).counter().count();
        Assertions.assertTrue(resultOpt.isPresent());
        Assertions.assertEquals(2, valor);
        UserJpaEntity result = resultOpt.get();
        Assertions.assertEquals(UserStatus.REJECTED, result.getStatus());
    }

    @Test
    @DisplayName("Should block user if as reached max rejection attempts and increment seller reject metrics")
    public void shouldBlockUserIfAsReachedMaxRejectionAttemptsAndIncrementSellerRejectMetrics() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String sellerId = entity.getId().toString();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        entity.setStatus(UserStatus.AWAITING_APPROVAL);
        userJpaRepository.save(entity);

        SellerRejectionJpaEntity sellerRejectionJpaEntity = new SellerRejectionJpaEntity();
        sellerRejectionJpaEntity.setSeller(entity);
        sellerRejectionJpaEntity.setReasonsForRejections(List.of("any_reason", "any_reason", "any_reason"));
        sellerRejectionJpaEntity.setNumberOfRejections(3);
        sellerRejectionJpaRepository.save(sellerRejectionJpaEntity);

        UserJpaEntity sa = VALID_SUPER_ADMIN_JPA;
        userJpaRepository.save(sa);
        String token = jwtHelper.generate(sa.getId().toString(), sa.getRole(), sa.getStatus().name(), sa.getEmail());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String reasonForRejection = "any_reason";
        Map<String, Object> body = new HashMap<>();
        body.put("reasonForRejection", reasonForRejection);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/" + sellerId + "/reject"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Optional<UserJpaEntity> resultOpt = userJpaRepository.findById(entity.getId());
        double valor = registry.get("auth.seller.rejected").tag("reason", reasonForRejection).counter().count();
        Assertions.assertTrue(resultOpt.isPresent());
        Assertions.assertEquals(1, valor);
        UserJpaEntity result = resultOpt.get();
        Assertions.assertEquals(UserStatus.BLOCKED, result.getStatus());
    }

    //  Resubmit Seller
    @Test
    @DisplayName("Should throw AccessDeniedException if no auth token is provided on ResubmitSeller")
    public void shouldThrowAccessDeniedExceptionIfNoAuthTokenIsProvidedOnApproveSellerOnResubmitSeller() {
        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/resubmit"),
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if logged user is not seller")
    public void shouldThrowAccessDeniedExceptionIfLoggedUserIsNotSeller() throws IOException {
        //Creating non seller user
        BuyerJpaEntity entity = (BuyerJpaEntity) VALID_BUYER_JPA();
        ;
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setDeliveryAddresses(List.of(addressJpaEntity));
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        //Build request
        String frontImageName = "bi_frente.png";
        String backImageName = "bi_verso.png";

        byte[] biFrontBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + frontImageName)
                .readAllBytes();

        byte[] biBackBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + backImageName)
                .readAllBytes();


        ResubmitSellerRequest request = new ResubmitSellerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS_REQUEST);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        body.add("data", new HttpEntity<>(request, dataHeaders));

        HttpHeaders frontHeaders = new HttpHeaders();
        frontHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

        HttpHeaders backHeaders = new HttpHeaders();
        backHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

        body.add("biFrontPhoto", new HttpEntity<>(new NamedByteArrayResource(biFrontBytes, frontImageName), frontHeaders));
        body.add("biBackPhoto", new HttpEntity<>(new NamedByteArrayResource(biBackBytes, backImageName), backHeaders));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/resubmit"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("Access denied", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if logged seller is not in status REJECTED")
    public void shouldThrowAccessDeniedExceptionIfLoggedSellerIsNotInStatusRejected() throws IOException {
        //Creating non seller user
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        entity.setStatus(UserStatus.AWAITING_APPROVAL);
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        //Build request
        String frontImageName = "bi_frente.png";
        String backImageName = "bi_verso.png";

        byte[] biFrontBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + frontImageName)
                .readAllBytes();

        byte[] biBackBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + backImageName)
                .readAllBytes();


        ResubmitSellerRequest request = new ResubmitSellerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS_REQUEST);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        body.add("data", new HttpEntity<>(request, dataHeaders));

        HttpHeaders frontHeaders = new HttpHeaders();
        frontHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

        HttpHeaders backHeaders = new HttpHeaders();
        backHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

        body.add("biFrontPhoto", new HttpEntity<>(new NamedByteArrayResource(biFrontBytes, frontImageName), frontHeaders));
        body.add("biBackPhoto", new HttpEntity<>(new NamedByteArrayResource(biBackBytes, backImageName), backHeaders));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/resubmit"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("Access denied", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should update seller status to AWAITING_APPROVAL and send email")
    public void shouldUpdateSellerStatusToAwaitingApprovalAndSendEmail() throws IOException {
        //Creating non seller user
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        entity.setStatus(UserStatus.REJECTED);
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        //Build request
        String frontImageName = "bi_frente.png";
        String backImageName = "bi_verso.png";

        byte[] biFrontBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + frontImageName)
                .readAllBytes();

        byte[] biBackBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + backImageName)
                .readAllBytes();


        ResubmitSellerRequest request = new ResubmitSellerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS_REQUEST);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        body.add("data", new HttpEntity<>(request, dataHeaders));

        HttpHeaders frontHeaders = new HttpHeaders();
        frontHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

        HttpHeaders backHeaders = new HttpHeaders();
        backHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

        body.add("biFrontPhoto", new HttpEntity<>(new NamedByteArrayResource(biFrontBytes, frontImageName), frontHeaders));
        body.add("biBackPhoto", new HttpEntity<>(new NamedByteArrayResource(biBackBytes, backImageName), backHeaders));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ApiResponse<ResubmitSellerOutput>> response = restTemplate.exchange(
                url("/sellers/resubmit"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<ResubmitSellerOutput>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Seller resubmitted successfully", response.getBody().data().message());
        double valor = registry.get("auth.seller.resubmitted").counter().count();
        Assertions.assertEquals(1, valor);
        Optional<UserJpaEntity> resultOpt = userJpaRepository.findById(entity.getId());
        Assertions.assertTrue(resultOpt.isPresent());
        UserJpaEntity result = resultOpt.get();
        Assertions.assertEquals(UserStatus.AWAITING_APPROVAL, result.getStatus());
    }

    @Test
    @DisplayName("Should update seller status to PENDING and send activation token email")
    public void shouldUpdateSellerStatusToPendingAndSendActivationTokenEmail() throws IOException {
        FuncUtils funcUtils = new FuncUtils();
        //Creating non seller user
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        entity.setStatus(UserStatus.REJECTED);
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        //Build request
        String frontImageName = "bi_frente.png";
        String backImageName = "bi_verso.png";

        byte[] biFrontBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + frontImageName)
                .readAllBytes();

        byte[] biBackBytes = getClass().getClassLoader()
                .getResourceAsStream("fixtures/images/" + backImageName)
                .readAllBytes();


        ResubmitSellerRequest request = new ResubmitSellerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, "dombo@gmail.com", VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS_REQUEST);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders dataHeaders = new HttpHeaders();
        dataHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

        body.add("data", new HttpEntity<>(request, dataHeaders));

        HttpHeaders frontHeaders = new HttpHeaders();
        frontHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

        HttpHeaders backHeaders = new HttpHeaders();
        backHeaders.setContentType(org.springframework.http.MediaType.IMAGE_PNG);

        body.add("biFrontPhoto", new HttpEntity<>(new NamedByteArrayResource(biFrontBytes, frontImageName), frontHeaders));
        body.add("biBackPhoto", new HttpEntity<>(new NamedByteArrayResource(biBackBytes, backImageName), backHeaders));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);

        ResponseEntity<ApiResponse<ResubmitSellerOutput>> response = restTemplate.exchange(
                url("/sellers/resubmit"),
                HttpMethod.PATCH,
                new HttpEntity<>(body, headers),
                new ParameterizedTypeReference<ApiResponse<ResubmitSellerOutput>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("As you changed you email we've sent instruction to conform account in the provided email.", response.getBody().data().message());

        double valor = registry.get("auth.seller.resubmitted").counter().count();
        Assertions.assertEquals(1, valor);

        Optional<UserJpaEntity> resultOpt = userJpaRepository.findById(entity.getId());
        Assertions.assertTrue(resultOpt.isPresent());
        UserJpaEntity result = resultOpt.get();
        Assertions.assertEquals(UserStatus.PENDING, result.getStatus());

        Optional<ActivationTokenJpaEntity> activationTokenOpt = activationTokenRepository.findByUsedAndUserId(false, entity.getId());
        Assertions.assertTrue(activationTokenOpt.isPresent());

        Optional<OutboxEventJpaEntity> outboxEventOpt = outboxEventJpaRepository.findByAggregateId(entity.getId());
        Assertions.assertTrue(outboxEventOpt.isPresent());
    }

    //    UPDATE SELLER
    @Test
    @DisplayName("Should throw AccessDeniedException if no auth token is provided on update seller")
    public void shouldThrowAccessDeniedExceptionIfNoAuthTokenIsProvidedOnUpdateSeller() {
        UpdateSellerRequest request = new UpdateSellerRequest(
                VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_STORE_NAME, VALID_STORE_DESC
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/update"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should throw AccessDeniedException if logged user is not seller on update seller")
    public void shouldThrowAccessDeniedExceptionIfLoggedUserIsNotSellerOnUpdateSeller() {
        BuyerJpaEntity entity = (BuyerJpaEntity) VALID_BUYER_JPA();
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setDeliveryAddresses(List.of(addressJpaEntity));
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        UpdateSellerRequest request = new UpdateSellerRequest(
                VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_STORE_NAME, VALID_STORE_DESC
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiResponse<Object>> response = restTemplate.exchange(
                url("/sellers/update"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<Object>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        Assertions.assertEquals("Access denied", response.getBody().error().message());
    }

    @Test
    @DisplayName("Should update seller fields and set AWAITING_APPROVAL when email is unchanged")
    public void shouldUpdateSellerFieldsAndSetAwaitingApprovalWhenEmailIsUnchanged() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        entity.setStatus(UserStatus.ACTIVE);
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        String newStoreName = "Nova Loja Dombo";
        String newStoreDescription = "Nova descrição da loja";

        UpdateSellerRequest request = new UpdateSellerRequest(
                null, null, null, null, newStoreName, newStoreDescription
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiResponse<UpdateSellerOutput>> response = restTemplate.exchange(
                url("/sellers/update"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<UpdateSellerOutput>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Seller updated successfully", response.getBody().data().message());

        Optional<UserJpaEntity> resultOpt = userJpaRepository.findById(entity.getId());
        Assertions.assertTrue(resultOpt.isPresent());
        SellerJpaEntity result = (SellerJpaEntity) resultOpt.get();
        Assertions.assertEquals(UserStatus.AWAITING_APPROVAL, result.getStatus());
        Assertions.assertEquals(newStoreName, result.getStoreName());
        Assertions.assertEquals(newStoreDescription, result.getStoreDescription());
    }

    @Test
    @DisplayName("Should set seller status to PENDING and send activation token email when email changes")
    public void shouldSetSellerStatusToPendingAndSendActivationTokenEmailWhenEmailChangesOnUpdateSeller() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        AddressJpaEntity addressJpaEntity = UserMapper.toAddressJpaEntity(VALID_ADDRESS_NEW);
        addressJpaRepository.save(addressJpaEntity);
        entity.setStoreAddress(addressJpaEntity);
        entity.setStatus(UserStatus.ACTIVE);
        userJpaRepository.save(entity);

        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());

        UpdateSellerRequest request = new UpdateSellerRequest(
                null, null, "dombo.novo@gmail.com", null, null, null
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<ApiResponse<UpdateSellerOutput>> response = restTemplate.exchange(
                url("/sellers/update"),
                HttpMethod.PATCH,
                new HttpEntity<>(request, headers),
                new ParameterizedTypeReference<ApiResponse<UpdateSellerOutput>>() {
                }
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(
                "As you changed you email we've sent instruction to conform account in the provided email.",
                response.getBody().data().message()
        );

        Optional<UserJpaEntity> resultOpt = userJpaRepository.findById(entity.getId());
        Assertions.assertTrue(resultOpt.isPresent());
        Assertions.assertEquals(UserStatus.PENDING, resultOpt.get().getStatus());

        Optional<ActivationTokenJpaEntity> activationTokenOpt =
                activationTokenRepository.findByUsedAndUserId(false, entity.getId());
        Assertions.assertTrue(activationTokenOpt.isPresent());

        Optional<OutboxEventJpaEntity> outboxEventOpt = outboxEventJpaRepository.findByAggregateId(entity.getId());
        Assertions.assertTrue(outboxEventOpt.isPresent());
    }
}