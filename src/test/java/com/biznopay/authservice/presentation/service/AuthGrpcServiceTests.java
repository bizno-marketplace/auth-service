package com.biznopay.authservice.presentation.service;

import com.biznopay.authservice._config.ContainerBase;
import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.grpc.*;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static com.biznopay.authservice.testcases.SellerTestCases.VALID_SELLER_JPA;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN_JPA;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@ActiveProfiles("grpc-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class AuthGrpcServiceTests extends ContainerBase {
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private MeterRegistry registry;
    @Autowired
    private UserJpaRepository userJpaRepository;

    private ManagedChannel channel;
    private AuthServiceGrpc.AuthServiceBlockingStub stub;

    @BeforeEach
    public void setUp() {
        channel = ManagedChannelBuilder
                .forAddress("localhost", 9099)
                .usePlaintext()
                .build();
        stub = AuthServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    public void tearDown() {
        if (channel != null)
            channel.shutdownNow();
    }

    //    VALIDATE TOKEN
    @Test
    @DisplayName("Should return valid when token is real valid")
    public void shouldReturnValidWhenTokenIsRealValid() {
        SellerJpaEntity entity = (SellerJpaEntity) VALID_SELLER_JPA;
        String token = jwtHelper.generate(entity.getId().toString(), entity.getRole(), entity.getStatus().name(), entity.getEmail());
        ValidateTokenRequest request = ValidateTokenRequest.newBuilder().setToken(token).build();
        ValidateTokenResponse response = stub.validateToken(request);
        Assertions.assertTrue(response.getValid());

        Timer timer = registry.find("auth.grpc.validate_token.duration").timer();
        org.assertj.core.api.Assertions.assertThat(timer).isNotNull();
    }

    @Test
    @DisplayName("Should return invalid when token is not valid")
    public void shouldReturnInvalidWhenTokenIsNotValid() {
        ValidateTokenRequest request = ValidateTokenRequest.newBuilder().setToken("invalid").build();
        ValidateTokenResponse response = stub.validateToken(request);
        Assertions.assertFalse(response.getValid());
    }


    @Test
    @DisplayName("Should throw invalid argument when token is empty")
    void shouldThrowInvalidArgumentWhenTokenIsEmpty() {
        ValidateTokenRequest request = ValidateTokenRequest.newBuilder()
                .setToken("")
                .build();

        assertThatThrownBy(() -> stub.validateToken(request))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("INVALID_ARGUMENT")
                .hasMessage("INVALID_ARGUMENT: Token is required");
    }

    //    GET USER PROFILE
    @Test
    @DisplayName("Should throw RequiredFieldException if user id is empty")
    public void shouldThrowRequiredFieldExceptionIfUserIdIsEmpty() {
        String userId = "";
        GetUserProfileRequest request = GetUserProfileRequest.newBuilder()
                .setUserId(userId)
                .build();

        assertThatThrownBy(() -> stub.getUserProfile(request))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("INVALID_ARGUMENT")
                .hasMessage("INVALID_ARGUMENT: UserId is required");
    }

    @Test
    @DisplayName("Should throw InvalidFieldException if user id is invalid")
    public void shouldThrowInvalidFieldExceptionIfUserIdIsInvalid() {
        String userId = "invalid_user_id";
        GetUserProfileRequest request = GetUserProfileRequest.newBuilder()
                .setUserId(userId)
                .build();

        assertThatThrownBy(() -> stub.getUserProfile(request))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("INVALID_ARGUMENT")
                .hasMessage("INVALID_ARGUMENT: Invalid UserId on AuthGrpcService");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if user does not exists")
    public void shouldThrowResourceNotFoundExceptionIfUserDoesNotExists() {
        String userId = UUID.randomUUID().toString();
        GetUserProfileRequest request = GetUserProfileRequest.newBuilder()
                .setUserId(userId)
                .build();

        assertThatThrownBy(() -> stub.getUserProfile(request))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND")
                .hasMessage("NOT_FOUND: User not found");
    }

    @Test
    @DisplayName("Should return saved user if exists")
    public void shouldReturnSavedUserIfExists() {
        SuperAdminJpaEntity entity = (SuperAdminJpaEntity) VALID_SUPER_ADMIN_JPA;
        String userId = entity.getId().toString();
        userJpaRepository.save(entity);

        GetUserProfileRequest request = GetUserProfileRequest.newBuilder().setUserId(userId).build();
        GetUserProfileResponse response = stub.getUserProfile(request);

        Assertions.assertEquals(entity.getId().toString(), response.getUserId());
        Assertions.assertEquals(entity.getEmail(), response.getEmail());
        Assertions.assertEquals(entity.getFirstName(), response.getFirstName());
        Assertions.assertEquals(entity.getLastName(), response.getLastName());
        Assertions.assertEquals(Role.SUPER_ADMIN.name(), response.getRole());
        Assertions.assertEquals(entity.getStatus().name(), response.getStatus());
    }
}
