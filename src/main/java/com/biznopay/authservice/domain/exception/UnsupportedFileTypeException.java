package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class UnsupportedFileTypeException extends BiznoException {
    public UnsupportedFileTypeException(String fieldName, String entity, String type, String code) {
        String message = "File type not supported form field " + fieldName + ". Only " + type + " are allowed";
        super(message, code, ExceptionSeverity.LOW, Map.of(entity, fieldName));
    }
}
