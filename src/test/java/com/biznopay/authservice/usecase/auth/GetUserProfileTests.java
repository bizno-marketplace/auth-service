package com.biznopay.authservice.usecase.auth;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.usecase.auth.getUserProfile.GetUserProfile;
import com.biznopay.authservice.usecase.auth.getUserProfile.GetUserProfileInput;
import com.biznopay.authservice.usecase.auth.getUserProfile.GetUserProfileOutput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class GetUserProfileTests {
    @InjectMocks
    private GetUserProfile usecase;

    @Mock
    private UserGateway userGateway;

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user is not found")
    public void shouldThrowResourceNotFoundExceptionWhenUserIsNotFound() {
        UUID userId = UUID.randomUUID();
        GetUserProfileInput input = new GetUserProfileInput(userId.toString());
        Mockito.when(userGateway.findById(userId)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> usecase.execute(input));
    }

    @Test
    @DisplayName("Should return user profile when user is found")
    public void shouldReturnUserProfileWhenIsFound() {
        User user = VALID_SUPER_ADMIN;
        UUID userId = user.getId().value();
        GetUserProfileInput input = new GetUserProfileInput(userId.toString());
        Mockito.when(userGateway.findById(userId)).thenReturn(Optional.of(user));
        GetUserProfileOutput output = usecase.execute(input);
        Assertions.assertEquals(output.userId(), userId.toString());
        Assertions.assertEquals(output.email(), user.getEmail());
        Assertions.assertEquals(output.firstName(), user.getFirstName());
        Assertions.assertEquals(output.lastName(), user.getLastName());
        Assertions.assertEquals(output.role(), user.getRole().name());
        Assertions.assertEquals(output.status(), user.getStatus().name());
    }
}
