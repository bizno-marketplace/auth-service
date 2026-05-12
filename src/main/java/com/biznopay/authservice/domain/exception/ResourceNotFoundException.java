package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class ResourceNotFoundException extends BiznoException {
    public ResourceNotFoundException(String entity, String code) {
        String message = entity + " not found";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
