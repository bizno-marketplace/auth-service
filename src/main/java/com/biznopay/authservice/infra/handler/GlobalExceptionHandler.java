package com.biznopay.authservice.infra.handler;

import com.biznopay.authservice.domain.exception.InvalidPasswordException;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.NonBiznoInstitutionalEmailException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.ApiError;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.util.FuncUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation error");

        ApiError error = new ApiError("VALIDATION_ERROR", errorMessage);
        log.warn("[LOW] {} {} | code=VALIDATION_ERROR | errors={}", request.getMethod(), request.getRequestURI(), error);
        return ResponseEntity.badRequest().body(FuncUtils.buildResponseBody(false, null, error));
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Object>> handleRequiredFieldException(RequiredFieldException exception, HttpServletRequest request) {
        log.warn("[{}] {} {} | code={} | field={} | message={}",
                exception.getSeverity(), request.getMethod(), request.getRequestURI(),
                exception.getErrorCode(), exception.getMetadata(), exception.getMessage());

        ApiError error = new ApiError(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.badRequest().body(FuncUtils.buildResponseBody(false, null, error));
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ResponseEntity<ApiResponse<Object>> handleInvalidStringFieldLengException(InvalidStringFieldLengException exception, HttpServletRequest request) {
        log.warn("[{}] {} {} | code={} | field={} | message={}",
                exception.getSeverity(), request.getMethod(), request.getRequestURI(),
                exception.getErrorCode(), exception.getMetadata(), exception.getMessage());

        ApiError error = new ApiError(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.unprocessableContent().body(FuncUtils.buildResponseBody(false, null, error));
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ResponseEntity<ApiResponse<Object>> handleNonBiznoInstitutionalEmailException(NonBiznoInstitutionalEmailException exception, HttpServletRequest request) {
        log.warn("[{}] {} {} | code={} | field={} | message={}",
                exception.getSeverity(), request.getMethod(), request.getRequestURI(),
                exception.getErrorCode(), exception.getMetadata(), exception.getMessage());

        ApiError error = new ApiError(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.unprocessableContent().body(FuncUtils.buildResponseBody(false, null, error));
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ResponseEntity<ApiResponse<Object>> handleInvalidPasswordExceptionException(InvalidPasswordException exception, HttpServletRequest request) {
        log.warn("[{}] {} {} | code={} | field={} | message={}",
                exception.getSeverity(), request.getMethod(), request.getRequestURI(),
                exception.getErrorCode(), exception.getMetadata(), exception.getMessage());

        ApiError error = new ApiError(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.unprocessableContent().body(FuncUtils.buildResponseBody(false, null, error));
    }
}
