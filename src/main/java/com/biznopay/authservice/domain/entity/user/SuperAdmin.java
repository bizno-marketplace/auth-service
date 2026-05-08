package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.NonBiznoInstitutionalEmailException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;

public class SuperAdmin extends User {
    private static final String BIZNO_EMAIL_DOMAIN = "@bizno.co.mz";

    private SuperAdmin(UserId id, String firstName, String lastname, String email, String phone, String password, UserStatus status,
                       LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastname, email, phone, password, status, expiresAt, createdAt, updatedAt);
    }

    public static SuperAdmin register(String firstName, String lastname, String email, String password) {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(2);
        email = validateBiznoInstitutionalEmail(email);
        return new SuperAdmin(UserId.generate(), firstName, lastname, email, "", password, UserStatus.PENDING, expiresAt, createdAt, createdAt);
    }

    public static SuperAdmin reconstitute(UserId id, String firstName, String lastName, String email, String phone,
                                          String password, UserStatus status, LocalDateTime expiresAt,
                                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new SuperAdmin(id, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
    }

    private static String validateBiznoInstitutionalEmail(String email) {
        if (email == null || email.isEmpty())
            throw new RequiredFieldException("Email", User.class.getName(), "SUPER_ADMIN-001");
        if (!email.endsWith(BIZNO_EMAIL_DOMAIN)) throw new NonBiznoInstitutionalEmailException("SUPER_ADMIN-002");
        return email;
    }
}
