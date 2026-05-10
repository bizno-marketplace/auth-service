package com.biznopay.authservice.domain.vo;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError error,
        Instant timestamp
) {
}
