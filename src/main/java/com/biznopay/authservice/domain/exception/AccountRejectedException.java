package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class AccountRejectedException extends BiznoException {
    public AccountRejectedException(String code) {
        String message = "Cannot approve a rejected seller account";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}