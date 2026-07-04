package com.biznopay.authservice.domain.gateway;

import java.util.function.Supplier;

public interface TransactionGateway {
    <T> T execute(Supplier<T> operation);

    void execute(Runnable operation);
}
