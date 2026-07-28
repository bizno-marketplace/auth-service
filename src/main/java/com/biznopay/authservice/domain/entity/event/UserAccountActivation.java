package com.biznopay.authservice.domain.entity.event;

import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.InvalidEmailException;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserAccountActivation {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";

    private final UUID eventId;
    private final UserId userId;
    private final String email;
    private final String firstName;
    private final ActivationTokenId activationTokenId;
    private final LocalDateTime occurredAt;
    private final String eventType;
    private final String sourceService;

    private UserAccountActivation(UUID eventId, UserId userId, String email, String firstName, ActivationTokenId activationTokenId,
                                  LocalDateTime occurredAt, String eventType, String sourceService) {
        this.eventId = eventId;
        this.userId = this.validateUserId(userId);
        this.email = this.validateEmail(email);
        this.firstName = this.validateFirstName(firstName);
        this.activationTokenId = this.validateActivationTokenId(activationTokenId);
        this.occurredAt = occurredAt;
        this.eventType = this.validateEventType(eventType);
        this.sourceService = this.validateSourceService(sourceService);
    }

    public static UserAccountActivation of(UserId userId, String email, String firstName, ActivationTokenId activationTokenId, String eventType) {
        return new UserAccountActivation(UUID.randomUUID(), userId, email, firstName, activationTokenId, LocalDateTime.now(), eventType, "auth-service");
    }

    private UserId validateUserId(UserId userId) {
        if (userId == null)
            throw new RequiredFieldException("UserId", UserAccountActivation.class.getName(), "USER_REGISTERED-001");
        return userId;
    }

    private String validateEmail(String email) {
        if (email == null || email.isEmpty())
            throw new RequiredFieldException("E-mail", UserAccountActivation.class.getName(), "USER_REGISTERED-002");
        if (!email.matches(EMAIL_REGEX))
            throw new InvalidEmailException("USER_REGISTERED-003");
        return email;
    }

    private String validateFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty())
            throw new RequiredFieldException("FirstName", UserAccountActivation.class.getName(), "USER_REGISTERED-004");
        if (firstName.length() < 3)
            throw new InvalidStringFieldLengException("FirstName", 3, UserAccountActivation.class.getName(), "USER_REGISTERED-005");
        return firstName;
    }

    private ActivationTokenId validateActivationTokenId(ActivationTokenId activationTokenId) {
        if (activationTokenId == null)
            throw new RequiredFieldException("ActivationTokenId", UserAccountActivation.class.getName(), "USER_REGISTERED-006");
        return activationTokenId;
    }

    private String validateEventType(String eventType) {
        if (eventType == null || eventType.isEmpty())
            throw new RequiredFieldException("Event type", UserAccountActivation.class.getName(), "USER_REGISTERED-007");
        return eventType;
    }

    private String validateSourceService(String sourceService) {
        if (sourceService == null || sourceService.isEmpty())
            throw new RequiredFieldException("Source service", UserAccountActivation.class.getName(), "USER_REGISTERED-008");
        return sourceService;
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

    public String getEventType() {
        return eventType;
    }

    public String getSourceService() {
        return sourceService;
    }
}
