package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class NotAuthorizedException extends BiznoException {
    public NotAuthorizedException(String message, String code) {
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
