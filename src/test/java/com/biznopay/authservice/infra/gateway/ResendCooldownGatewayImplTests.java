package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.gateway.ResendCooldownGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;

import static com.biznopay.authservice.infra.gateway.ResendCooldownGatewayImpl.KEY_PREFIX;

@ExtendWith(MockitoExtension.class)
public class ResendCooldownGatewayImplTests {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("Should return false when activation token is not in cooldown")
    public void shouldReturnFalseWhenActivationTokenIsNotInCooldown() {
        String email = "test@example.com";
        Mockito.when(stringRedisTemplate.hasKey(KEY_PREFIX + email)).thenReturn(false);
        ResendCooldownGateway resendCooldownGateway =  new ResendCooldownGatewayImpl(stringRedisTemplate);
        boolean result = resendCooldownGateway.isInCooldown(email);
        Assertions.assertFalse(result);
    }
}
