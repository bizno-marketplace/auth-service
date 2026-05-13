package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class ExpiredConfirmationTokenException extends BiznoException {
    public ExpiredConfirmationTokenException(String code) {
        String message = "Confirmation link expired";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
