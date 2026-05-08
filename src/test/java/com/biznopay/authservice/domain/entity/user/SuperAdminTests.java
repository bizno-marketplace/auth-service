package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class SuperAdminTests {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if firstName is null or empty on register")
    public void shouldThrowRequiredFieldExceptionIfFirstNameIsNullOrEmptyOnRegister(String firstName) {
        Assertions.assertThrows(RequiredFieldException.class,
                () -> SuperAdmin.register(firstName, "any_last_name", "any_email", "any_password"));
    }
}
