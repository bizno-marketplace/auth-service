package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class ConflictException extends BiznoException {
    public ConflictException(String entity, String code) {
        String message = entity + " already exists";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}