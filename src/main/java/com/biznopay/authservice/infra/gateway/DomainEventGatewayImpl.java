package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.event.UserAccountActivationEvent;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.commons.outbox.domain.enums.OutboxStatus;
import com.biznopay.commons.outbox.persistence.jpa.entity.OutboxEventJpaEntity;
import com.biznopay.commons.outbox.persistence.jpa.repository.OutboxEventJpaRepository;
import com.biznopay.shared.utils.FuncUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventGatewayImpl implements DomainEventGateway {
    private final OutboxEventJpaRepository repository;
    private final FuncUtils outboxFuncUtils;

    @Override
    public void publish(UserAccountActivationEvent event) {
        String payload = outboxFuncUtils.serialize(event);
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        entity.setAggregateId(event.getUserId().value());
        entity.setEventType(event.getEventType());
        entity.setStatus(OutboxStatus.PENDING);
        entity.setSubject(event.getSubject());
        entity.setPayload(payload);
        repository.save(entity);
    }
}
