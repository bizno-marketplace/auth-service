package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;

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
        return new SuperAdmin(UserId.generate(), firstName, lastname, email, "", password, UserStatus.PENDING, expiresAt, createdAt, createdAt);
    }
}
