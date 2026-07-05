package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.event.UserUpdated;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.vo.UserRegisteredPayload;
import com.biznopay.authservice.domain.vo.UserUpdatedPayload;
import com.biznopay.authservice.infra.mapper.OutboxEventMapper;
import com.biznopay.authservice.infra.outbox.OutboxEvent;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class DomainEventGatewayImpl implements DomainEventGateway {
    public static final String SUBJECT = "notifications.email.account-activation";
    public static final String EVENT_TYPE_USER_REGISTERED = "USER_REGISTERED";
    public static final String EVENT_TYPE_USER_UPDATED = "USER_UPDATED";


    private final OutboxEventJpaRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(UserRegistered event) {
        String payload = serialize(event);
        OutboxEvent outboxEvent = OutboxEvent.create(event.getUserId().value(), EVENT_TYPE_USER_REGISTERED, SUBJECT, payload);
        OutboxEventJpaEntity entity = OutboxEventMapper.toJpaEntity(outboxEvent);
        repository.save(entity);
    }

    @Override
    public void publish(UserUpdated event) {
        String payload = serialize(event);
        OutboxEvent outboxEvent = OutboxEvent.create(event.getUserId().value(), EVENT_TYPE_USER_UPDATED, SUBJECT, payload);
        OutboxEventJpaEntity entity = OutboxEventMapper.toJpaEntity(outboxEvent);
        repository.save(entity);
    }

    private String serialize(UserRegistered event) {
        return objectMapper.writeValueAsString(new UserRegisteredPayload(
                event.getEventId().toString(),
                event.getUserId().value().toString(),
                event.getEmail(),
                event.getFirstName(),
                event.getActivationTokenId().value().toString(),
                event.getOccurredAt().toString()
        ));
    }

    private String serialize(UserUpdated event) {
        return objectMapper.writeValueAsString(new UserUpdatedPayload(
                event.getEventId().toString(),
                event.getUserId().value().toString(),
                event.getEmail(),
                event.getFirstName(),
                event.getActivationTokenId().value().toString(),
                event.getOccurredAt().toString()
        ));
    }
}
