package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class InvalidEmailException extends BiznoException {
    public InvalidEmailException(String code) {
        String message = "Invalid E-mail";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
