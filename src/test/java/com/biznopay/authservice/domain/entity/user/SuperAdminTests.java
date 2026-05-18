package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidEntityIdException;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.NonBiznoInstitutionalEmailException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
public class SuperAdminTests {

    @Test
    @DisplayName("Should throw InvalidEntityIdException if id is invalid on build")
    public void shouldThrowInvalidEntityIdExceptionIfEntityIsUnknownOnBuild() {
        UserJpaEntity entity = new SuperAdminJpaEntity();
        Assertions.assertThrows(InvalidEntityIdException.class, () -> UserMapper.toUserDomain(entity));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if firstName is null or empty on register")
    public void shouldThrowRequiredFieldExceptionIfFirstNameIsNullOrEmptyOnRegister(String firstName) {
        Assertions.assertThrows(RequiredFieldException.class,
                () -> SuperAdmin.register(firstName, "any_last_name", "anybizno@bizno.co.mz", "any_password"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ad"})
    @DisplayName("Should throw InvalidStringFieldLengException if firstName has less than min characters allowed")
    public void shouldThrowInvalidStringFieldLengExceptionIfFirstNameHasLessThanMinCharactersAllowed(String firstName) {
        Assertions.assertThrows(InvalidStringFieldLengException.class,
                () -> SuperAdmin.register(firstName, "any_last_name", "anybizno@bizno.co.mz", "any_password"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if lastName is null or empty on register")
    public void shouldThrowRequiredFieldExceptionIfLastNameIsNullOrEmptyOnRegister(String lastName) {
        Assertions.assertThrows(RequiredFieldException.class,
                () -> SuperAdmin.register("any_first_name", lastName, "anybizno@bizno.co.mz", "any_password"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"ad"})
    @DisplayName("Should throw InvalidStringFieldLengException if lastName has less than min characters allowed")
    public void shouldThrowInvalidStringFieldLengExceptionIFlastNameHasLessThanMinCharactersAllowed(String lastName) {
        Assertions.assertThrows(InvalidStringFieldLengException.class,
                () -> SuperAdmin.register("any_first_name", lastName, "anybizno@bizno.co.mz", "any_password"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"any_email", "test@email.com", "someone@gmail.com"})
    @DisplayName("Should throw NonBiznoInstitutionalEmailException if email is not bizno instututional")
    public void shouldThrowNonBiznoInstitutionalEmailExceptionIfEmailIsNotBiznoInstitutional(String email) {
        Assertions.assertThrows(NonBiznoInstitutionalEmailException.class,
                () -> SuperAdmin.register("any_first_name", "any_last_name", email, "any_password"));
    }

    @Test
    @DisplayName("Should register SuperAdmin with correct param on register ande set status  PENDING")
    public void shouldRegisterSuperAdminWithCorrectParamOnRegisterAndSetStatusOnPENDING() {
        SuperAdmin superAdmin = SuperAdmin.register("any_first_name", "any_last_name", "anybizno@bizno.co.mz", "Password@123");
        Assertions.assertNotNull(superAdmin);
        Assertions.assertNotNull(superAdmin.getId());
        Assertions.assertEquals("any_first_name", superAdmin.getFirstName());
        Assertions.assertEquals("any_last_name", superAdmin.getLastName());
        Assertions.assertEquals("anybizno@bizno.co.mz", superAdmin.getEmail());
        Assertions.assertEquals("", superAdmin.getPhone());
        Assertions.assertEquals("Password@123", superAdmin.getPassword());
        Assertions.assertEquals(UserStatus.PENDING, superAdmin.getStatus());
        Assertions.assertNotNull(superAdmin.getExpiresAt());
        Assertions.assertNotNull(superAdmin.getCreatedAt());
        Assertions.assertNotNull(superAdmin.getUpdatedAt());
    }

    @Test
    @DisplayName("Should active SuperAdmin on active")
    public void shouldActiveSuperAdminOnActive() {
        SuperAdmin superAdmin = SuperAdmin.register("any_first_name", "any_last_name", "anybizno@bizno.co.mz", "Password@123");
        superAdmin.activate();
        Assertions.assertEquals(UserStatus.ACTIVE, superAdmin.getStatus());
    }
}
