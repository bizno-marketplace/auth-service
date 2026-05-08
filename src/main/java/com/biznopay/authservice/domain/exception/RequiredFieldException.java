package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class RequiredFieldException extends BiznoException {
    public RequiredFieldException(String fieldName, String entity, String code) {
        String message = fieldName + " is required";
        super(message, code, ExceptionSeverity.LOW, Map.of(entity, fieldName));
    }
}
