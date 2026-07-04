package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class BlockedAccountException extends BiznoException {
    public BlockedAccountException(String code) {
        String message = "Account permanently blocked";
        super(message, code, ExceptionSeverity.LOW, null);
    }
}