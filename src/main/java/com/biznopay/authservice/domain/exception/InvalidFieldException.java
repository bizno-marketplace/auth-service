package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class InvalidFieldException extends BiznoException {
    public InvalidFieldException(String fieldName, String entity, String code) {
        String message = "Invalid " + fieldName + " on " + entity;
        super(message, code, ExceptionSeverity.LOW, Map.of(entity, fieldName));
    }
}
