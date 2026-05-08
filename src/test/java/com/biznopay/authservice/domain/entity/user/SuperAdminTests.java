package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.NonBiznoInstitutionalEmailException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class SuperAdminTests {

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
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if email is null or empty on register")
    public void shouldThrowRequiredFieldExceptionIfEmailIsNullOrEmptyOnRegister(String email) {
        Assertions.assertThrows(RequiredFieldException.class,
                () -> SuperAdmin.register("any_first_name", "any_last_name", email, "any_password"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"any_email", "test@email.com","someone@gmail.com"})
    @DisplayName("Should throw NonBiznoInstitutionalEmailException if email is not bizno instututional")
    public void shouldThrowNonBiznoInstitutionalEmailExceptionIfEmailIsNotBiznoInstitutional(String email) {
        Assertions.assertThrows(NonBiznoInstitutionalEmailException.class,
                () -> SuperAdmin.register("any_first_name", "any_last_name", email, "any_password"));
    }
}
