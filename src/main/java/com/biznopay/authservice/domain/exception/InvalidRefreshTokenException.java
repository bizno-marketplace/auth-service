package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class InvalidRefreshTokenException extends TechnicalException {
    public InvalidRefreshTokenException() {
        String message = "Invalid refresh accessToken";
        super(message, "INVALID-REFRESH-TOKEN", ExceptionSeverity.LOW, null);
    }
}
