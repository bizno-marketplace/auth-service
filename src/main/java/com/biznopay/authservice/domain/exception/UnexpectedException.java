package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class UnexpectedException extends TechnicalException {
    public UnexpectedException(String code) {
        String message = "Unexpected error! Please try again later.";
        super(message, code, ExceptionSeverity.CRITICAL, null);
    }
}
