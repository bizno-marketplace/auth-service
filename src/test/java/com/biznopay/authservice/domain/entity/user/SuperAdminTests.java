package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidEntityIdException;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.UUID;

@Tag("unit")
public class SuperAdminTests {

    @Test
    @DisplayName("Should throw InvalidEntityIdException if id is invalid on build")
    public void shouldThrowInvalidEntityIdExceptionIfEntityIsUnknownOnBuild() {
        UserJpaEntity entity = new SuperAdminJpaEntity();
        Assertions.assertThrows(InvalidEntityIdException.class, () -> UserMapper.toUserDomain(entity));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.SuperAdminTestCases#registerSuperAdminDomainCases")
    public void registerSuperAdminCases(String testName, String firstName, String lastName, String email, String password,
                                        Class<? extends Exception> expectedException, String expectedMessage) {


        if (testName.equals("Success")) {
            SuperAdmin superAdmin = SuperAdmin.register(firstName, lastName, email, password);
            Assertions.assertNotNull(superAdmin);
            Assertions.assertNotNull(superAdmin.getId());
            Assertions.assertEquals(firstName, superAdmin.getFirstName());
            Assertions.assertEquals(lastName, superAdmin.getLastName());
            Assertions.assertEquals(email, superAdmin.getEmail());
            Assertions.assertEquals("", superAdmin.getPhone());
            Assertions.assertEquals(password, superAdmin.getPassword());
            Assertions.assertEquals(UserStatus.PENDING, superAdmin.getStatus());
            Assertions.assertNotNull(superAdmin.getExpiresAt());
            Assertions.assertNotNull(superAdmin.getCreatedAt());
            Assertions.assertNotNull(superAdmin.getUpdatedAt());
        } else {
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> SuperAdmin.register(firstName, lastName, email, password))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedMessage);
        }
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.SuperAdminTestCases#reconstructDomainCases")
    public void reconstructDomainCases(String testName, UUID useId, String firstName, String lastName, String email,
                                       String phone, String password, UserStatus status, LocalDateTime expiresAt,
                                       LocalDateTime createdAt, LocalDateTime updatedAt,
                                       Class<? extends Exception> expectedException, String expectedMessage) {

        if (testName.equals("Success")) {
            SuperAdmin superAdmin = SuperAdmin.reconstruct(useId, firstName, lastName,
                    email, phone, password, status, expiresAt, createdAt, updatedAt);

            Assertions.assertNotNull(superAdmin);
            Assertions.assertNotNull(superAdmin.getId());
            Assertions.assertEquals(firstName, superAdmin.getFirstName());
            Assertions.assertEquals(lastName, superAdmin.getLastName());
            Assertions.assertEquals(email, superAdmin.getEmail());
            Assertions.assertEquals(phone, superAdmin.getPhone());
            Assertions.assertEquals(password, superAdmin.getPassword());
            Assertions.assertEquals(UserStatus.PENDING, superAdmin.getStatus());
            Assertions.assertNotNull(superAdmin.getExpiresAt());
            Assertions.assertNotNull(superAdmin.getCreatedAt());
            Assertions.assertNotNull(superAdmin.getUpdatedAt());
        } else if (testName.equals("Active")) {
            SuperAdmin superAdmin = SuperAdmin.reconstruct(useId, firstName, lastName,
                    email, phone, password, status, expiresAt, createdAt, updatedAt);
            superAdmin.activate();
            Assertions.assertEquals(UserStatus.ACTIVE, superAdmin.getStatus());
        } else if (testName.equals("Set to Awaiting for approval")) {
            SuperAdmin superAdmin = SuperAdmin.reconstruct(useId, firstName, lastName,
                    email, phone, password, status, expiresAt, createdAt, updatedAt);
            superAdmin.setToAwaitingForApproval();
            Assertions.assertEquals(UserStatus.AWAITING_APPROVAL, superAdmin.getStatus());
        } else {
            org.assertj.core.api.Assertions.assertThatThrownBy(() -> SuperAdmin.reconstruct(useId, firstName, lastName,
                            email, phone, password, status, expiresAt, createdAt, updatedAt))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedMessage);
        }
    }
}
