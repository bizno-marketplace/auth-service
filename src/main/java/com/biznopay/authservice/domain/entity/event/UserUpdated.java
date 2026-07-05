package com.biznopay.authservice.domain.entity.event;

import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.InvalidEmailException;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserUpdated {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";

    private final UUID eventId;
    private final UserId userId;
    private final String email;
    private final String firstName;
    private final ActivationTokenId activationTokenId;
    private final LocalDateTime occurredAt;

    private UserUpdated(UUID eventId, UserId userId, String email, String firstName, ActivationTokenId activationTokenId, LocalDateTime occurredAt) {
        this.eventId = eventId;
        this.userId = this.validateUserId(userId);
        this.email = this.validateEmail(email);
        this.firstName = this.validateFirstName(firstName);
        this.activationTokenId = this.validateActivationTokenId(activationTokenId);
        this.occurredAt = occurredAt;
    }

    public static UserUpdated of(UserId userId, String email, String firstName, ActivationTokenId activationTokenId) {
        return new UserUpdated(UUID.randomUUID(), userId, email, firstName, activationTokenId, LocalDateTime.now());
    }

    private UserId validateUserId(UserId userId) {
        if (userId == null)
            throw new RequiredFieldException("UserId", UserUpdated.class.getName(), "USER_UPDATED-001");
        return userId;
    }

    private String validateEmail(String email) {
        if (email == null || email.isEmpty())
            throw new RequiredFieldException("E-mail", UserUpdated.class.getName(), "USER_UPDATED-002");
        if (!email.matches(EMAIL_REGEX))
            throw new InvalidEmailException("USER_UPDATED-003");
        return email;
    }

    private String validateFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty())
            throw new RequiredFieldException("FirstName", UserUpdated.class.getName(), "USER_UPDATED-004");
        if (firstName.length() < 3)
            throw new InvalidStringFieldLengException("FirstName", 3, UserUpdated.class.getName(), "USER_UPDATED-005");
        return firstName;
    }

    private ActivationTokenId validateActivationTokenId(ActivationTokenId activationTokenId) {
        if (activationTokenId == null)
            throw new RequiredFieldException("ActivationTokenId", UserUpdated.class.getName(), "USER_UPDATED-006");
        return activationTokenId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UserId getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public ActivationTokenId getActivationTokenId() {
        return activationTokenId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
