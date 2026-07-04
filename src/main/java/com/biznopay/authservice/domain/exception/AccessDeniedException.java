package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class AccessDeniedException extends BiznoException {
    public AccessDeniedException(String code) {
        String message = "Access denied";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
