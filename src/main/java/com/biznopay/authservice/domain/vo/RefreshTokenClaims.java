package com.biznopay.authservice.domain.vo;

import java.util.UUID;

public record RefreshTokenClaims(UUID userId, String tokenId) {
}