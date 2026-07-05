package com.biznopay.authservice.usecase.auth.getUserProfile;

public record GetUserProfileOutput (
        String userId,
        String email,
        String firstName,
        String lastName,
        String role,
        String status
) {
}
