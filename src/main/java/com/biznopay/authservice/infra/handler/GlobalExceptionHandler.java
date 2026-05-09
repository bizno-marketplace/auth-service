package com.biznopay.authservice.infra.handler;

import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.vo.ApiError;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.util.FuncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ResponseEntity<ApiResponse<Object>> handleDomain(InvalidStringFieldLengException exception) {
        log.error("");
        ApiError error = new ApiError(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.unprocessableContent().body(FuncUtils.buildResponseBody(false, null, error));
    }
}
