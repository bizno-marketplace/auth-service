package com.biznopay.authservice.domain.exception;

import com.biznopay.authservice.domain.enums.ExceptionSeverity;

import java.util.Map;

public class FileSizeExceedLimitException extends BiznoException {
    public FileSizeExceedLimitException(String fieldName, String entity, long maxSize, String code) {
        String message = fieldName + " exceeds the maximum size of " + maxSize + "MB";
        super(message, code, ExceptionSeverity.LOW, Map.of(entity, fieldName));
    }
}
