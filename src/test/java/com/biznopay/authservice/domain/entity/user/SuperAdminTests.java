package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class SuperAdminTests {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if firstName is null or empty on register")
    public void shouldThrowRequiredFieldExceptionIfFirstNameIsNullOrEmptyOnRegister(String firstName) {
        Assertions.assertThrows(RequiredFieldException.class,
                () -> SuperAdmin.register(firstName, "any_last_name", "any_email", "any_password"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ad"})
    @DisplayName("Should throw InvalidStringFieldLengException if firstName has less than min characters allowed")
    public void shouldThrowInvalidStringFieldLengExceptionIfFirstNameHasLessThanMinCharactersAllowed(String firstName) {
        Assertions.assertThrows(InvalidStringFieldLengException.class,
                () -> SuperAdmin.register(firstName, "any_last_name", "any_email", "any_password"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if lastName is null or empty on register")
    public void shouldThrowRequiredFieldExceptionIfLastNameIsNullOrEmptyOnRegister(String lastName) {
        Assertions.assertThrows(RequiredFieldException.class,
                () -> SuperAdmin.register("any_first_name", lastName, "any_email", "any_password"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"ad"})
    @DisplayName("Should throw InvalidStringFieldLengException if lastName has less than min characters allowed")
    public void shouldThrowInvalidStringFieldLengExceptionIFlastNameHasLessThanMinCharactersAllowed(String lastName) {
        Assertions.assertThrows(InvalidStringFieldLengException.class,
                () -> SuperAdmin.register("any_first_name", lastName, "any_email", "any_password"));
    }

}
