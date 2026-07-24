package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class RefreshTokenExpiredException extends TechnicalException {
    public RefreshTokenExpiredException() {
        String message = "Refresh accessToken expired";
        super(message, "REFRESH-TOKEN-EXPIRED", ExceptionSeverity.MEDIUM, null);
    }
}
