package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class EmailAlreadyInUseException extends BiznoException {
    public EmailAlreadyInUseException(String code) {
        String message = "Email already in use";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
