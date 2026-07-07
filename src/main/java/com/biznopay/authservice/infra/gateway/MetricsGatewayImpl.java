package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.gateway.MetricsGateway;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class MetricsGatewayImpl implements MetricsGateway {
    private final MeterRegistry registry;

    @Override
    public void incrementSellerRegistered() {
        registry.counter("auth.seller.registered").increment();
    }

    @Override
    public void incrementSellerApproved() {
        registry.counter("auth.seller.approved").increment();
    }

    @Override
    public void incrementSellerRejected(String reason) {
        registry.counter("auth.seller.rejected", "reason", reason).increment();
    }

    @Override
    public void incrementSellerResubmitted() {
        registry.counter("auth.seller.resubmitted").increment();
    }

    @Override
    public void incrementBuyerRegistered() {
        registry.counter("auth.buyer.registered").increment();
    }

    @Override
    public void recordValidateTokenDuration(long durationMillis) {
        registry.timer("auth.grpc.validate_token.duration")
                .record(Duration.ofMillis(durationMillis));
    }
}
