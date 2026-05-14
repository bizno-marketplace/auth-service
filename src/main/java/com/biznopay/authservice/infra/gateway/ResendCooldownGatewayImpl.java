package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.gateway.ResendCooldownGateway;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@AllArgsConstructor
public class ResendCooldownGatewayImpl implements ResendCooldownGateway {
    public static final String KEY_PREFIX = "resend-cooldown:";
    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isInCooldown(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + email));
    }

    @Override
    public void startCooldown(String email, Duration duration) {
        redisTemplate.opsForValue().set(KEY_PREFIX + email, "1", duration);
    }
}