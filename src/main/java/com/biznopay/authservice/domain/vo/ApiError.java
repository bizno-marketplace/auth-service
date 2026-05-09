package com.biznopay.authservice.domain.vo;

public record ApiError(
        String code,
        String message
) {
}
