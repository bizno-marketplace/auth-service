package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.NonBiznoInstitutionalEmailException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.presentation.dto.RegisterSARequest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public class SuperAdminTestCases {
    public static final UUID VALID_USER_ID = UUID.randomUUID();
    public static final String VALID_FIRST_NAME = "Super";
    public static final String VALID_LAST_NAME = "Admin";
    public static final String VALID_EMAIL = "superadmin@bizno.co.mz";
    public static final String VALID_PHONE = "+258841234567";
    public static final UserStatus VALID_STATUS = UserStatus.ACTIVE;
    public static final String VALID_PASSWORD = "Segura@123";
    public static final LocalDateTime VALID_EXPIRES_AT = LocalDateTime.now();
    public static final LocalDateTime VALID_CREATED_AT = LocalDateTime.now();
    public static final LocalDateTime VALID_UPDATED_AT = LocalDateTime.now();
    public static final RegisterSARequest VALID_REGISTER_SA_REQUEST = new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD);
    public static final SuperAdmin VALID_SUPER_ADMIN = SuperAdmin.reconstruct(VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT);
    public static final SuperAdmin VALID_SUPER_ADMIN_NEW = SuperAdmin.register(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD);
    public static final UserJpaEntity VALID_SUPER_ADMIN_JPA = UserMapper.toUserJpaEntity(VALID_SUPER_ADMIN_NEW);


    public static Stream<Arguments> registerSuperAdminDomainCases() {
        return Stream.of(
                Arguments.of("First name is null", null, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is empty", "", VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is less than min characters", "ad", VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, InvalidStringFieldLengException.class, "First name must be at least 3 characters long"),
                Arguments.arguments("Last name is null", VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PASSWORD, RequiredFieldException.class, "Last name is required"),
                Arguments.arguments("Last name is empty", VALID_FIRST_NAME, "", VALID_EMAIL, VALID_PASSWORD, RequiredFieldException.class, "Last name is required"),
                Arguments.arguments("Last name is less than min characters", VALID_FIRST_NAME, "ad", VALID_EMAIL, VALID_PASSWORD, InvalidStringFieldLengException.class, "Last name must be at least 3 characters long"),
                Arguments.arguments("Email is not bizno institutional", VALID_FIRST_NAME, VALID_LAST_NAME, "ad", VALID_PASSWORD, NonBiznoInstitutionalEmailException.class, "E-mail must be a bizno institutional email"),
                Arguments.arguments("Success", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, null, null)
        );
    }

    public static Stream<Arguments> reconstructDomainCases() {
        return Stream.of(
                Arguments.of("First name is null", VALID_USER_ID, null, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is empty", VALID_USER_ID, "", VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is less than min characters", VALID_USER_ID, "Jo", VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, InvalidStringFieldLengException.class, "First name must be at least 3 characters long"),
                Arguments.arguments("Last name is null", VALID_USER_ID, VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "Last name is required"),
                Arguments.arguments("Last name is empty", VALID_USER_ID, VALID_FIRST_NAME, "", VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "Last name is required"),
                Arguments.arguments("Last name is less than min characters", VALID_USER_ID, VALID_FIRST_NAME, "Jo", VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, InvalidStringFieldLengException.class, "Last name must be at least 3 characters long"),
                Arguments.arguments("Email is not bizno institutional", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, "test@gmail.com", VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, NonBiznoInstitutionalEmailException.class, "E-mail must be a bizno institutional email"),
                Arguments.arguments("Success", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, null, null),
                Arguments.arguments("Active", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, null, null),
                Arguments.arguments("Set to Awaiting for approval", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STATUS, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, null, null)

        );
    }


    public static Stream<Arguments> registerSControllerCases() {
        return Stream.of(
                Arguments.of("First name is null", new RegisterSARequest(null, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD), HttpStatus.BAD_REQUEST, "First name is required"),
                Arguments.of("First name is empty", new RegisterSARequest("", VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD), HttpStatus.BAD_REQUEST, "First name is required"),
                Arguments.of("First name too short", new RegisterSARequest("Su", VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD), HttpStatus.UNPROCESSABLE_CONTENT, "First name must be at least 3 characters long"),
                Arguments.of("Last name is null", new RegisterSARequest(VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PASSWORD), HttpStatus.BAD_REQUEST, "Last name is required"),
                Arguments.of("Last name is empty", new RegisterSARequest(VALID_FIRST_NAME, "", VALID_EMAIL, VALID_PASSWORD), HttpStatus.BAD_REQUEST, "Last name is required"),
                Arguments.of("Last name too short", new RegisterSARequest(VALID_FIRST_NAME, "Ad", VALID_EMAIL, VALID_PASSWORD), HttpStatus.UNPROCESSABLE_CONTENT, "Last name must be at least 3 characters long"),
                Arguments.of("Email is null", new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, null, VALID_PASSWORD), HttpStatus.BAD_REQUEST, "E-mail is required"),
                Arguments.of("Email is empty", new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, "", VALID_PASSWORD), HttpStatus.BAD_REQUEST, "E-mail is required"),
                Arguments.of("Email is invalid", new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, "admin@gmail.com", VALID_PASSWORD), HttpStatus.UNPROCESSABLE_CONTENT, "E-mail must be a bizno institutional email"),
                Arguments.of("Password is null", new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, null), HttpStatus.BAD_REQUEST, "Password is required"),
                Arguments.of("Password is empty", new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, ""), HttpStatus.BAD_REQUEST, "Password is required"),
                Arguments.of("Password is weak", new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "password"), HttpStatus.UNPROCESSABLE_CONTENT, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"),
                Arguments.of("Conflict SA exists", new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD), HttpStatus.CONFLICT, "Super admin already exists"),
                Arguments.of("Success", new RegisterSARequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD), HttpStatus.OK, "We've sent an activation link to provided email: " + VALID_EMAIL)
        );
    }
}
