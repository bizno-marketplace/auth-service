package com.biznopay.authservice.infra.outbox;

import com.biznopay.authservice.infra.mapper.OutboxEventMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.OutboxEventJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.OutboxEventJpaRepository;
import io.nats.client.Connection;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPoller {
    public static final String LOCK_KEY = "outbox-poller-lock";
    public static final long LOCK_TTL_MS = 4000;
    private static final Logger log = LoggerFactory.getLogger(OutboxPoller.class);
    private final OutboxEventJpaRepository repository;
    private final Connection natsConnection;
    private final StringRedisTemplate redisTemplate;

    @Scheduled(fixedDelayString = "${outbox.poller.interval-ms:5000}")
    public void poll() {
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(LOCK_KEY, "locked", Duration.ofMillis(LOCK_TTL_MS));

        if (Boolean.FALSE.equals(acquired)) return;

        try {
            List<OutboxEventJpaEntity> pending = repository.findByStatus(OutboxStatus.PENDING);
            for (OutboxEventJpaEntity entity : pending) {
                OutboxEvent event = OutboxEventMapper.toDomain(entity);
                process(event);
                repository.save(OutboxEventMapper.toJpaEntity(event));
            }
        } finally {
            redisTemplate.delete(LOCK_KEY);
        }
    }

    private void process(OutboxEvent event) {
        try {
            natsConnection.publish(event.getSubject(), event.getPayload().getBytes());
            event.markPublished();
            log.info("[OUTBOX] Event published — type={} subject={} aggregateId={}",
                    event.getEventType(), event.getSubject(), event.getAggregateId());
        } catch (Exception e) {
            event.registerFailure(e.getMessage());
            if (event.hasExhaustedRetries()) {
                log.error("[OUTBOX] Event FAILED after {} retries — type={} aggregateId={} lastError={}",
                        event.getRetryCount(), event.getEventType(),
                        event.getAggregateId(), e.getMessage());
            } else {
                log.warn("[OUTBOX] Retry {}/{} — type={} aggregateId={} error={}",
                        event.getRetryCount(), OutboxEvent.MAX_RETRIES,
                        event.getEventType(), event.getAggregateId(), e.getMessage());
            }
        }
    }
}