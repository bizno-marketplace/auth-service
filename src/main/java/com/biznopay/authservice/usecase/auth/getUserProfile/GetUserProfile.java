package com.biznopay.authservice.usecase.auth.getUserProfile;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.UUID;

public class GetUserProfile {
    private final UserGateway userGateway;

    public GetUserProfile(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public GetUserProfileOutput execute(GetUserProfileInput input) {
        UUID userId = UUID.fromString(input.userId());
        User user = getUserById(userId);
        return buildOutput(user);
    }

    private User getUserById(UUID userId) {
        return userGateway.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException("User", "GET_USER_PROFILE-001"));
    }

    private GetUserProfileOutput buildOutput(User user) {
        return new GetUserProfileOutput(
                user.getId().value().toString(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }
}
