package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class InvalidNuitException extends BiznoException {
    public InvalidNuitException(String entity, String code) {
        String message = "NUIT must contain only digits and must be exactly 9 digits";
        super(message, code, ExceptionSeverity.LOW, Map.of(entity, "nuit"));
    }
}
