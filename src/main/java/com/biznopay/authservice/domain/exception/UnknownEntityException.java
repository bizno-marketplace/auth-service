package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;


public class UnknownEntityException extends BiznoException {
    public UnknownEntityException(String entity, String code) {
        String message = "Unknown entity " +  entity;
        super(message, code, ExceptionSeverity.LOW, null);
    }
}
