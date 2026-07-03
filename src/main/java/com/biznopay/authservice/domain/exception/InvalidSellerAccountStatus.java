package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

public class InvalidSellerAccountStatus extends BiznoException {
    public InvalidSellerAccountStatus(String status, String code) {
        String message = "Can only perform this action to Sellers with status " + status;
        super(message, code, ExceptionSeverity.LOW, null);
    }
}