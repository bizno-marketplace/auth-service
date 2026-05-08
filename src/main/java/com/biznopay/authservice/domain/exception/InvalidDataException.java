package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class InvalidDataException extends BiznoException {
    public InvalidDataException(String entity, String code) {
        String message =  "Invalid id for entity: " + entity;
        super(message, code, ExceptionSeverity.LOW, Map.of(entity, "Id"));
    }
}
