package com.biznopay.authservice.presentation.validator;

import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.grpc.GetUserProfileRequest;
import com.biznopay.authservice.grpc.ValidateTokenRequest;

import java.util.UUID;

public class AuthGrpcServiceValidator {
    public static void validateTokenRequestValidator(ValidateTokenRequest request) {
        if (request.getToken() == null || request.getToken().isEmpty())
            throw new RequiredFieldException("Token", "AuthGrpcService", "AUTH_GRPC_SERVICE-001");
    }

    public static void getUserProfileRequestValidator(GetUserProfileRequest request) {
        if (request.getUserId() == null || request.getUserId().isEmpty())
            throw new RequiredFieldException("UserId", "AuthGrpcService", "AUTH_GRPC_SERVICE-002");
        try {
            UUID.fromString(request.getUserId());
        } catch (Exception e) {
            throw new InvalidFieldException("UserId", "AuthGrpcService", "AUTH_GRPC_SERVICE-003");
        }
    }
}
