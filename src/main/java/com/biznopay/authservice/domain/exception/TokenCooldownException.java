package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class TokenCooldownException extends BiznoException{
    public TokenCooldownException(String code) {
        String message ="Please wait before requesting a new confirmation email";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
