package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.UnknownEntityException;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.presentation.dto.RegisterSARequest;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.biznopay.authservice.testcases.BuyerTestCases.VALID_BUYER;
import static com.biznopay.authservice.testcases.BuyerTestCases.VALID_BUYER_JPA;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.*;

class MockUnknownDomainEntityException extends User {
    public MockUnknownDomainEntityException(UserId id, String firstName, String lastname, String email, String phone,
                                            String password, UserStatus status, LocalDateTime expiresAt,
                                            LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastname, email, phone, password, Role.SELLER, status, expiresAt, createdAt, updatedAt);
    }

    public static MockUnknownDomainEntityException register(String firstName, String lastname, String email, String password) {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(2);
        return new MockUnknownDomainEntityException(UserId.generate(), firstName, lastname, email, "", password, UserStatus.PENDING, expiresAt, createdAt, createdAt);
    }
}

class MockUnknownJpaEntityException extends UserJpaEntity {
    public MockUnknownJpaEntityException(UUID id, String firstName, String lastName, String email, String phone,
                                         String password, UserStatus status, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
    }

    public MockUnknownJpaEntityException() {
    }
}

@Tag("unit")
public class UserMapperTests {
    @Test
    @DisplayName("Should return Super admin jpa entity on toUserJpaEntity")
    public void shouldReturnSuperAdminJpaEntityOnToUserJpaEntity() {
        UserMapper userMapper = new UserMapper();
        User user = VALID_SUPER_ADMIN;
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        Assertions.assertInstanceOf(SuperAdminJpaEntity.class, entity);
        Assertions.assertEquals(user.getId().value(), entity.getId());
        Assertions.assertEquals(user.getFirstName(), entity.getFirstName());
        Assertions.assertEquals(user.getLastName(), entity.getLastName());
        Assertions.assertEquals(user.getEmail(), entity.getEmail());
        Assertions.assertEquals(user.getPhone(), entity.getPhone());
        Assertions.assertEquals(user.getPassword(), entity.getPassword());
        Assertions.assertEquals(user.getStatus(), entity.getStatus());
        Assertions.assertEquals(user.getExpiresAt(), entity.getExpiresAt());
        Assertions.assertEquals(user.getCreatedAt(), entity.getCreatedAt());
        Assertions.assertEquals(user.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return Buyer jpa entity on toUserJpaEntity")
    public void shouldReturnBuyerJpaEntityOnToUserJpaEntity() {
        User user = VALID_BUYER;
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        Assertions.assertInstanceOf(UserJpaEntity.class, entity);
        Assertions.assertEquals(user.getId().value(), entity.getId());
        Assertions.assertEquals(user.getFirstName(), entity.getFirstName());
        Assertions.assertEquals(user.getLastName(), entity.getLastName());
        Assertions.assertEquals(user.getEmail(), entity.getEmail());
        Assertions.assertEquals(user.getPhone(), entity.getPhone());
        Assertions.assertEquals(user.getPassword(), entity.getPassword());
        Assertions.assertEquals(user.getStatus(), entity.getStatus());
        Assertions.assertEquals(user.getExpiresAt(), entity.getExpiresAt());
        Assertions.assertEquals(user.getCreatedAt(), entity.getCreatedAt());
        Assertions.assertEquals(user.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw UnknownEntityException if entity is unknown on toUserJpaEntity")
    public void shouldThrowUnknownEntityExceptionIfEntityIsUnknownOnToUserJpaEntity() {
        User user = MockUnknownDomainEntityException.register("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        Assertions.assertThrows(UnknownEntityException.class, () -> UserMapper.toUserJpaEntity(user));
    }

    @Test
    @DisplayName("Should return Super admin domain entity on toUserDomain")
    public void shouldReturnSuperAdminEntityOnToUserDomain() {
        UserJpaEntity entity = VALID_SUPER_ADMIN_JPA;
        User user = UserMapper.toUserDomain(entity);
        Assertions.assertInstanceOf(SuperAdmin.class, user);
        Assertions.assertEquals(entity.getId(), user.getId().value());
        Assertions.assertEquals(entity.getFirstName(), user.getFirstName());
        Assertions.assertEquals(entity.getLastName(), user.getLastName());
        Assertions.assertEquals(entity.getEmail(), user.getEmail());
        Assertions.assertEquals(user.getPhone(), user.getPhone());
        Assertions.assertEquals(entity.getPassword(), user.getPassword());
        Assertions.assertEquals(entity.getStatus(), user.getStatus());
        Assertions.assertEquals(entity.getExpiresAt(), user.getExpiresAt());
        Assertions.assertEquals(entity.getCreatedAt(), user.getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return Buyer domain entity on toUserDomain")
    public void shouldReturnBuyerEntityOnToUserDomain() {
        UserJpaEntity entity = VALID_BUYER_JPA;
        User user = UserMapper.toUserDomain(entity);
        Assertions.assertInstanceOf(Buyer.class, user);
        Assertions.assertEquals(entity.getId(), user.getId().value());
        Assertions.assertEquals(entity.getFirstName(), user.getFirstName());
        Assertions.assertEquals(entity.getLastName(), user.getLastName());
        Assertions.assertEquals(entity.getEmail(), user.getEmail());
        Assertions.assertEquals(user.getPhone(), user.getPhone());
        Assertions.assertEquals(entity.getPassword(), user.getPassword());
        Assertions.assertEquals(entity.getStatus(), user.getStatus());
        Assertions.assertEquals(entity.getExpiresAt(), user.getExpiresAt());
        Assertions.assertEquals(entity.getCreatedAt(), user.getCreatedAt());
        Assertions.assertEquals(entity.getUpdatedAt(), user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw UnknownEntityException if entity is unknown on toUserDomain")
    public void shouldThrowUnknownEntityExceptionIfEntityIsUnknownOnToUserDomain() {
        UserJpaEntity entity = new MockUnknownJpaEntityException(UUID.randomUUID(), "any_first_name", "any_last_name",
                "admin@bizno.co.mz", "", "Password@123", UserStatus.PENDING, LocalDateTime.now().plusDays(2),
                LocalDateTime.now(), LocalDateTime.now());
        Assertions.assertThrows(UnknownEntityException.class, () -> UserMapper.toUserDomain(entity));
    }

    @Test
    @DisplayName("Should return RegisterSAInput on toRegisterSAInput")
    public void shouldReturnRegisterSAInputOnToRegisterSAInput() {
        RegisterSARequest request = VALID_REGISTER_SA_REQUEST;
        RegisterSAInput input = new RegisterSAInput(request.firstName(), request.lastName(), request.email(), request.password());
        Assertions.assertEquals(request.firstName(), input.firstName());
        Assertions.assertEquals(request.lastName(), input.lastName());
        Assertions.assertEquals(request.email(), input.email());
        Assertions.assertEquals(request.password(), input.password());
    }
}
