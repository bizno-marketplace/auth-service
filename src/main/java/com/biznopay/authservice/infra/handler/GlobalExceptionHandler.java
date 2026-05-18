package com.biznopay.authservice.infra.handler;

import com.biznopay.authservice.domain.exception.*;
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleExceptions(RuntimeException exception, HttpServletRequest request) {
        return switch (exception) {
            case RequiredFieldException ex -> FuncUtils.handleBadRequest(ex, request, log);
            case InvalidConfirmationTokenException ex -> FuncUtils.handleBadRequest(ex, request, log);
            case InvalidEmailException ex -> FuncUtils.handleBadRequest(ex, request, log);
            case ConflictException ex -> FuncUtils.handleConflict(ex, request, log);
            case EmailAlreadyInUseException ex -> FuncUtils.handleConflict(ex, request, log);
            case AccountAlreadyConfirmedException ex -> FuncUtils.handleConflict(ex, request, log);
            case ExpiredConfirmationTokenException ex -> FuncUtils.handleGone(ex, request, log);
            case InvalidStringFieldLengException ex -> FuncUtils.handleUnprocessableContent(ex, request, log);
            case NonBiznoInstitutionalEmailException ex -> FuncUtils.handleUnprocessableContent(ex, request, log);
            case InvalidPasswordException ex -> FuncUtils.handleUnprocessableContent(ex, request, log);
            case InvalidFieldException ex -> FuncUtils.handleUnprocessableContent(ex, request, log);
            case TokenCooldownException ex -> FuncUtils.handleToManyRequests(ex, request, log);
            default -> FuncUtils.handleUnexpectedException(exception, request, log);
        };
    }
}
