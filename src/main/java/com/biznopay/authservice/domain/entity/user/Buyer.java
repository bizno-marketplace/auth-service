package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;

import java.time.LocalDateTime;

public class Buyer extends User {

    private Buyer(UserId id, String firstName, String lastname, String email, String phone, String password, UserStatus status,
                  LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastname, email, phone, password, status, expiresAt, createdAt, updatedAt);
    }

    public static Buyer register(String firstName, String lastname, String email, String password) {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(2);
        return new Buyer(UserId.generate(), firstName, lastname, email, "", password, UserStatus.PENDING, expiresAt, createdAt, createdAt);
    }

    public static Buyer reconstitute(UserId id, String firstName, String lastName, String email, String phone,
                                     String password, UserStatus status, LocalDateTime expiresAt,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Buyer(id, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
    }
}
