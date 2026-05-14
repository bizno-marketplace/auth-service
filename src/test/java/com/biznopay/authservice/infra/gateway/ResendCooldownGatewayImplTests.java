package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.gateway.ResendCooldownGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static com.biznopay.authservice.infra.gateway.ResendCooldownGatewayImpl.KEY_PREFIX;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ResendCooldownGatewayImplTests {
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    @DisplayName("Should return false when activation token is not in cooldown")
    public void shouldReturnFalseWhenActivationTokenIsNotInCooldown() {
        String email = "test@example.com";
        when(redisTemplate.hasKey(KEY_PREFIX + email)).thenReturn(false);
        ResendCooldownGateway resendCooldownGateway = new ResendCooldownGatewayImpl(redisTemplate);
        boolean result = resendCooldownGateway.isInCooldown(email);
        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when activation token is in cooldown")
    public void shouldReturnTrueWhenActivationTokenIsInCooldown() {
        String email = "test@example.com";
        when(redisTemplate.hasKey(KEY_PREFIX + email)).thenReturn(true);
        ResendCooldownGateway resendCooldownGateway = new ResendCooldownGatewayImpl(redisTemplate);
        boolean result = resendCooldownGateway.isInCooldown(email);
        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Should start cooldown with correct values")
    public void shouldStartCooldownWithCorrectValues() {
        String email = "test@example.com";
        Duration duration = Duration.ofMinutes(1);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        ResendCooldownGateway resendCooldownGateway = new ResendCooldownGatewayImpl(redisTemplate);
        resendCooldownGateway.startCooldown(email, duration);
        Mockito.verify(valueOperations).set("resend-cooldown:" + email, "1", duration);
    }
}
