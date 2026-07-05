package com.biznopay.authservice.infra.util;

import com.biznopay.authservice.domain.exception.*;
import com.biznopay.authservice.domain.vo.ApiError;
import com.biznopay.authservice.domain.vo.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

public class FuncUtils {
    public static ApiResponse<Object> buildResponseBody(boolean success, Object data, ApiError error) {
        return new ApiResponse<Object>(success, data, error, Instant.now());
    }

    public static ResponseEntity<ApiResponse<Object>> handleBadRequest(RuntimeException exception, HttpServletRequest request, Logger log) {
        ApiError error = null;
        if (exception instanceof RequiredFieldException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), ex.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof InvalidConfirmationTokenException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), ex.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }
        return ResponseEntity.badRequest().body(FuncUtils.buildResponseBody(false, null, error));
    }

    public static ResponseEntity<ApiResponse<Object>> handleNotFound(RuntimeException exception, HttpServletRequest request, Logger log) {
        ApiError error = null;
        if (exception instanceof ResourceNotFoundException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), ex.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        return new ResponseEntity<>(FuncUtils.buildResponseBody(false, null, error), HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<ApiResponse<Object>> handleConflict(RuntimeException exception, HttpServletRequest request, Logger log) {
        ApiError error = null;
        if (exception instanceof ConflictException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof EmailAlreadyInUseException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof AccountAlreadyConfirmedException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof NuitAlreadyInUseException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof InvalidSellerAccountStatus ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        return new ResponseEntity<>(FuncUtils.buildResponseBody(false, null, error), HttpStatus.CONFLICT);
    }

    public static ResponseEntity<ApiResponse<Object>> handleGone(ExpiredConfirmationTokenException exception, HttpServletRequest request, Logger log) {
        ApiError error = new ApiError(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(FuncUtils.buildResponseBody(false, null, error));
    }

    public static ResponseEntity<ApiResponse<Object>> handleUnprocessableContent(RuntimeException exception, HttpServletRequest request, Logger log) {
        ApiError error = null;
        if (exception instanceof InvalidEmailException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), ex.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof InvalidStringFieldLengException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof InvalidPasswordException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof NonBiznoInstitutionalEmailException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof InvalidFieldException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof InvalidPhoneNumberException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        if (exception instanceof InvalidNuitException ex) {
            log.warn("[{}] {} {} | code={} | field={} | message={}",
                    ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                    ex.getErrorCode(), ex.getMetadata(), exception.getMessage());
            error = new ApiError(ex.getErrorCode(), exception.getMessage());
        }

        return ResponseEntity.unprocessableContent().body(FuncUtils.buildResponseBody(false, null, error));
    }

    public static ResponseEntity<ApiResponse<Object>> handleToManyRequests(TokenCooldownException exception, HttpServletRequest request, Logger log) {
        log.warn("[{}] {} {} | code={} | field={} | message={}",
                exception.getSeverity(), request.getMethod(), request.getRequestURI(),
                exception.getErrorCode(), exception.getMetadata(), exception.getMessage());
        ApiError error = new ApiError(exception.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(FuncUtils.buildResponseBody(false, null, error));
    }


    public static ResponseEntity<ApiResponse<Object>> handleForbidden(AccessDeniedException ex, HttpServletRequest request, Logger log) {
        log.warn("[{}] {} {} | code={} | field={} | message={}",
                ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                ex.getErrorCode(), ex.getMetadata(), ex.getMessage());
        ApiError error = new ApiError(ex.getErrorCode(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(FuncUtils.buildResponseBody(false, null, error));
    }

    public static ResponseEntity<ApiResponse<Object>> handleUnexpectedException(RuntimeException exception, HttpServletRequest request, Logger log) {
        TechnicalException ex = new UnexpectedException("UNEXPECTED_ERROR-001");
        log.error("[{}] {} {} | code={} | message={} | metadata={}",
                ex.getSeverity(), request.getMethod(), request.getRequestURI(),
                ex.getErrorCode(), exception.getMessage(), ex.getMetadata());
        ApiError error = new ApiError(ex.getErrorCode(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FuncUtils.buildResponseBody(false, null, error));
    }
}