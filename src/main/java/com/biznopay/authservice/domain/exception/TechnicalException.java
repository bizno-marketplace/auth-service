package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public abstract class TechnicalException extends RuntimeException {
    private final String errorCode;
    private final ExceptionSeverity severity;
    private  final Map<String, String> metadata;

    protected TechnicalException(String message, String errorCode, ExceptionSeverity severity, Map<String, String> metadata) {
        super(message);
        this.errorCode = errorCode;
        this.severity = severity;
        this.metadata = metadata;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public ExceptionSeverity getSeverity() {
        return severity;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }
}