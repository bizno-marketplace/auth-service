package com.biznopay.authservice.domain.entity.event;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserRegistered {
    private final UUID eventId;
    private final UserId userId;
    private final String email;
    private final String firstName;
    private final ActivationTokenId activationTokenId;
    private final LocalDateTime occurredAt;

    private UserRegistered(UUID eventId, UserId userId, String email, String firstName, ActivationTokenId activationTokenId, LocalDateTime occurredAt) {
        this.eventId = eventId;
        this.userId = this.validateUserId(userId);
        this.email = email;
        this.firstName = firstName;
        this.activationTokenId = activationTokenId;
        this.occurredAt = occurredAt;
    }

    public static UserRegistered of(UserId userId, String email, String firstName, ActivationTokenId activationTokenId) {
        return new UserRegistered(UUID.randomUUID(), userId, email, firstName, activationTokenId, LocalDateTime.now());
    }

    private UserId validateUserId(UserId userId){
        if(userId == null)
            throw new RequiredFieldException("userId",UserRegistered.class.getName(),"USER_REGISTERED-001");
        return userId;
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

    public ActivationTokenId getActivationToken() {
        return activationTokenId;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }
}
