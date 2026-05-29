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

import static com.biznopay.authservice.testcases.SuperAdminTestCases.*;

@Tag("unit")
public class SuperAdminTests {

    @Test
    @DisplayName("Should throw InvalidEntityIdException if id is invalid on build")
    public void shouldThrowInvalidEntityIdExceptionIfEntityIsUnknownOnBuild() {
        UserJpaEntity entity = new SuperAdminJpaEntity();
        Assertions.assertThrows(InvalidEntityIdException.class, () -> UserMapper.toUserDomain(entity));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.SuperAdminTestCases#invalidDomainRegisterSuperAdminCases")
    public void shouldThrowExceptionWhenRegisterWithInvalidData(String message, String firstName, String lastName, String email, String password,
                                                                Class<? extends Exception> expectedException, String expectedMessage) {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> SuperAdmin.register(firstName, lastName, email, password))
                .isInstanceOf(expectedException)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Should register SuperAdmin with correct param on register ande set status  PENDING")
    public void shouldRegisterSuperAdminWithCorrectParamOnRegisterAndSetStatusOnPENDING() {
        SuperAdmin superAdmin = SuperAdmin.register(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD);
        Assertions.assertNotNull(superAdmin);
        Assertions.assertNotNull(superAdmin.getId());
        Assertions.assertEquals(VALID_FIRST_NAME, superAdmin.getFirstName());
        Assertions.assertEquals(VALID_LAST_NAME, superAdmin.getLastName());
        Assertions.assertEquals(VALID_EMAIL, superAdmin.getEmail());
        Assertions.assertEquals("", superAdmin.getPhone());
        Assertions.assertEquals(VALID_PASSWORD, superAdmin.getPassword());
        Assertions.assertEquals(UserStatus.PENDING, superAdmin.getStatus());
        Assertions.assertNotNull(superAdmin.getExpiresAt());
        Assertions.assertNotNull(superAdmin.getCreatedAt());
        Assertions.assertNotNull(superAdmin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should active SuperAdmin on active")
    public void shouldActiveSuperAdminOnActive() {
        SuperAdmin superAdmin = SuperAdmin.register(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD);
        superAdmin.activate();
        Assertions.assertEquals(UserStatus.ACTIVE, superAdmin.getStatus());
    }
}
