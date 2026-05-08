package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class InvalidEntityIdException extends BiznoException {
    public InvalidEntityIdException(String entity, String code) {
        String message =  "Invalid id for entity: " + entity;
        super(message, code, ExceptionSeverity.LOW, Map.of(entity, "Id"));
    }
}