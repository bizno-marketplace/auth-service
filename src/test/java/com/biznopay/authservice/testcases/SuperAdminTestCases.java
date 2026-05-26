package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.NonBiznoInstitutionalEmailException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class SuperAdminTestCases {
    public static final String VALID_FIRST_NAME = "Super";
    public static final String VALID_LAST_NAME = "Admin";
    public static final String VALID_EMAIL = "superadmin@bizno.co.mz";
    public static final String VALID_PASSWORD = "Segura@123";

    public static Stream<Arguments> invalidDomainRegisterSuperAdminCases() {
        return Stream.of(
                Arguments.of("First name is null", null, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is empty", "", VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is less than min characters", "ad", VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, InvalidStringFieldLengException.class, "First name must be at least 3 characters long"),
                Arguments.arguments("Last name is null", VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PASSWORD, RequiredFieldException.class, "Last name is required"),
                Arguments.arguments("Last name is empty", VALID_FIRST_NAME, "", VALID_EMAIL, VALID_PASSWORD, RequiredFieldException.class, "Last name is required"),
                Arguments.arguments("Last name is less than min characters", VALID_FIRST_NAME, "ad", VALID_EMAIL, VALID_PASSWORD, InvalidStringFieldLengException.class, "Last name must be at least 3 characters long"),
                Arguments.arguments("Email is not bizno institutional", VALID_FIRST_NAME, VALID_LAST_NAME, "ad", VALID_PASSWORD, NonBiznoInstitutionalEmailException.class, "E-mail must be a bizno institutional email")
        );
    }
}
