package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class InvalidPhoneNumberException extends BiznoException {
    public InvalidPhoneNumberException(String code) {
        String message = "Invalid phone number";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
