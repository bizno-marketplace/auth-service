package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class InvalidConfirmationTokenException extends BiznoException {
    public InvalidConfirmationTokenException(String code) {
        String message = "Invalid confirmation link";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
