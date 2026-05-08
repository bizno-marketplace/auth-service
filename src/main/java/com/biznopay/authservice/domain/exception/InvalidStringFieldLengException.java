package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class InvalidStringFieldLengException extends BiznoException {
    public InvalidStringFieldLengException(String fieldName, int length, String entity, String code) {
        String message = fieldName + "must be at least " + length + " characters long";
        super(message, code, ExceptionSeverity.LOW, Map.of(entity, fieldName));
    }
}
