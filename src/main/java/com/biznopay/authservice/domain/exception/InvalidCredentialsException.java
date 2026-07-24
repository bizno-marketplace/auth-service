package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class InvalidCredentialsException extends BiznoException {
    public InvalidCredentialsException(String code) {
        String message = "E-mail or password is incorrect";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
