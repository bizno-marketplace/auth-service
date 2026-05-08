package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;

public abstract class User {
    private final UserId id;
    private final String firstName;
    private final String lastname;
    private final String email;
    private final String phone;
    private final String password;
    private final UserStatus status;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    protected User(UserId id, String firstName, String lastname, String email, String phone, String password, UserStatus status,
                LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.firstName = this.validateFirstName(firstName);
        this.lastname = this.validateLastName(lastname);
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.status = status;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //START VALIDATIONS
    private String validateFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty())
            throw new RequiredFieldException("First name", User.class.getName(), "USER-002");
        if (firstName.length() < 3)
            throw new InvalidStringFieldLengException("First name", 3, User.class.getName(), "USER-003");

        return  firstName;
    }

    private String validateLastName(String lastname){
        if (lastname == null || lastname.isEmpty())
            throw new RequiredFieldException("Last name", User.class.getName(), "USER-004");
        if (lastname.length() < 3)
            throw new InvalidStringFieldLengException("Last name", 3, User.class.getName(), "USER-005");
        return lastname;
    }
    //END VALIDATIONS


    public UserId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public UserStatus getStatus() {
        return status;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
