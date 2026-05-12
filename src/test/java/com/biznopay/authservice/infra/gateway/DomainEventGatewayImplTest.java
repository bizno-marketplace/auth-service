package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.vo.UserRegisteredPayload;
import com.biznopay.authservice.infra.mapper.OutboxEventMapper;
import com.biznopay.authservice.infra.outbox.OutboxEvent;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.OutboxEventJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.biznopay.authservice.infra.gateway.DomainEventGatewayImpl.EVENT_TYPE_USER_REGISTERED;
import static com.biznopay.authservice.infra.gateway.DomainEventGatewayImpl.SUBJECT_USER_REGISTERED;

@ExtendWith(MockitoExtension.class)
public class DomainEventGatewayImplTest {
    @Mock
    private OutboxEventJpaRepository outboxEventJpaRepository;
    @Mock
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should save OutboxEvent with correct values")
    public void shouldSaveOutboxEventWithCorrectValues() {
        UserId userId =  UserId.of(UUID.randomUUID());
        ActivationTokenId activationTokenId = ActivationTokenId.of(UUID.randomUUID());
        UserRegistered event = UserRegistered.of(userId, "test@email.com", "firstName", activationTokenId);

        String payload = "payload";
        Mockito.when(objectMapper.writeValueAsString(Mockito.any(UserRegisteredPayload.class))).thenReturn(payload);
        OutboxEvent outboxEvent = OutboxEvent.create(event.getUserId().value(), EVENT_TYPE_USER_REGISTERED, SUBJECT_USER_REGISTERED, payload);
        OutboxEventJpaEntity entity = OutboxEventMapper.toJpaEntity(outboxEvent);

        Mockito.when(outboxEventJpaRepository.save(Mockito.any(OutboxEventJpaEntity.class))).thenReturn(entity);

        DomainEventGateway domainEventGateway =  new DomainEventGatewayImpl(outboxEventJpaRepository,objectMapper);
        domainEventGateway.publish(event);

        Mockito.verify(outboxEventJpaRepository).save(Mockito.any());
        Mockito.verify(objectMapper).writeValueAsString(Mockito.any());
    }
}
