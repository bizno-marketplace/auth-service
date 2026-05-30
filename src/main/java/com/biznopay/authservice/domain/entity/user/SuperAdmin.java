package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.NonBiznoInstitutionalEmailException;

import java.time.LocalDateTime;
import java.util.UUID;

public class SuperAdmin extends User {
    private static final String BIZNO_EMAIL_DOMAIN = "@bizno.co.mz";

    private SuperAdmin(UserId id, String firstName, String lastname, String email, String phone, String password, UserStatus status,
                       LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastname, email, phone, password, Role.SUPER_ADMIN, status, expiresAt, createdAt, updatedAt);
    }

    public static SuperAdmin register(String firstName, String lastname, String email, String password) {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(2);
        email = validateBiznoInstitutionalEmail(email);
        return new SuperAdmin(UserId.generate(), firstName, lastname, email, "", password, UserStatus.PENDING, expiresAt, createdAt, createdAt);
    }

    public static SuperAdmin reconstruct(UUID id, String firstName, String lastName, String email, String phone,
                                         String password, UserStatus status, LocalDateTime expiresAt,
                                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        UserId userId = UserId.of(id);
        email = validateBiznoInstitutionalEmail(email);
        return new SuperAdmin(userId, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
    }

    private static String validateBiznoInstitutionalEmail(String email) throws NonBiznoInstitutionalEmailException {
        if (!email.endsWith(BIZNO_EMAIL_DOMAIN))
            throw new NonBiznoInstitutionalEmailException("SUPER_ADMIN-002");
        return email;
    }
}
