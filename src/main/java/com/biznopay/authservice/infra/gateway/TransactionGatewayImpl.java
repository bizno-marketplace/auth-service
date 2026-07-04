package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.gateway.TransactionGateway;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class TransactionGatewayImpl implements TransactionGateway {
    @Override
    @Transactional
    public <T> T execute(Supplier<T> operation) {
        return operation.get();
    }

    @Override
    @Transactional
    public void execute(Runnable operation) {
        operation.run();
    }
}
