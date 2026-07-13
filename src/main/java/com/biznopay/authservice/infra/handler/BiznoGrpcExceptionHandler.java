package com.biznopay.authservice.infra.handler;

import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import io.grpc.Status;
import io.grpc.StatusException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BiznoGrpcExceptionHandler implements org.springframework.grpc.server.exception.GrpcExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BiznoGrpcExceptionHandler.class);

    @Override
    @Nullable
    public StatusException handleException(Throwable exception) {
        switch (exception) {
            case RequiredFieldException e -> {
                log.error("[{}] gRPC | code={} | message={}", e.getSeverity(), e.getErrorCode(), e.getMessage());
                return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e).asException();
            }
            case InvalidFieldException e -> {
                log.error("[{}] gRPC | code={} | message={}", e.getSeverity(), e.getErrorCode(), e.getMessage());
                return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).withCause(e).asException();
            }
            case ResourceNotFoundException e -> {
                log.error("[{}] gRPC | code={} | message={}", e.getSeverity(), e.getErrorCode(), e.getMessage());
                return Status.NOT_FOUND.withDescription(e.getMessage()).withCause(e).asException();
            }
            default -> {
            }
        }
        return null;
    }
}