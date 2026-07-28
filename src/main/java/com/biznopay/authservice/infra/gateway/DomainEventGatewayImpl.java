package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.event.UserAccountActivation;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.vo.UserAccountActivationPayload;
import com.biznopay.commons.outbox.domain.enums.OutboxStatus;
import com.biznopay.commons.outbox.persistence.jpa.entity.OutboxEventJpaEntity;
import com.biznopay.commons.outbox.persistence.jpa.repository.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class DomainEventGatewayImpl implements DomainEventGateway {
    public static final String SUBJECT = "notification.email.send";
    private final OutboxEventJpaRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(UserAccountActivation event) {
        String payload = serialize(event);
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        entity.setAggregateId(event.getUserId().value());
        entity.setEventType(event.getEventType());
        entity.setStatus(OutboxStatus.PENDING);
        entity.setSubject(SUBJECT);
        entity.setPayload(payload);
        repository.save(entity);
    }

    private String serialize(UserAccountActivation event) {
        return objectMapper.writeValueAsString(new UserAccountActivationPayload(
                event.getEventId().toString(),
                event.getUserId().value().toString(),
                event.getEmail(),
                event.getFirstName(),
                event.getActivationTokenId().value().toString(),
                event.getOccurredAt(),
                event.getEventType(),
                event.getSourceService()
        ));
    }
}
