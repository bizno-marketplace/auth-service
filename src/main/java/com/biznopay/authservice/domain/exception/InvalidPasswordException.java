package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class InvalidPasswordException extends BiznoException {
    public InvalidPasswordException(String code) {
        String message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character";
        super(message, code, ExceptionSeverity.LOW, Map.of("User", "Password"));
    }
}
