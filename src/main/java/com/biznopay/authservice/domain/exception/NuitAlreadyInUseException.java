package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class NuitAlreadyInUseException extends BiznoException {
    public NuitAlreadyInUseException(String code) {
        String message = "Nuit already in use";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
