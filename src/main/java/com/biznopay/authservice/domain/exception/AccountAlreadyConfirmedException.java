package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class AccountAlreadyConfirmedException extends BiznoException {
    public AccountAlreadyConfirmedException(String code) {
        String message = "Account already confirmed";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}