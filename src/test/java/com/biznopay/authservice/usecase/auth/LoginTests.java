package com.biznopay.authservice.usecase.auth;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.InvalidCredentialsException;
import com.biznopay.authservice.domain.exception.NotAuthorizedException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.EncoderGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.vo.AuthenticateOutput;
import com.biznopay.authservice.usecase.auth.login.Login;
import com.biznopay.authservice.usecase.auth.login.LoginInput;
import com.biznopay.authservice.usecase.auth.login.LoginOutput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.biznopay.authservice.testcases.SellerTestCases.VALID_SELLER;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class LoginTests {
    @Mock
    private UserGateway userGateway;

    @Mock
    private EncoderGateway encoderGateway;

    @Mock
    private AuthenticationGateway authenticationGateway;

    @InjectMocks
    private Login login;


    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException when email is null or empty")
    public void shouldThrowRequiredFieldExceptionWhenEmailIsNullOrEmpty(String email) {
        LoginInput input = new LoginInput(email, "password");
        Assertions.assertThatThrownBy(() -> login.execute(input))
                .isInstanceOf(RequiredFieldException.class)
                .hasMessage("E-mail is required");
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when user does not exists")
    public void shouldThrowInvalidCredentialsExceptionWhenUserDoesNotExists() {
        LoginInput input = new LoginInput("email", "password");
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> login.execute(input))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("E-mail or password is incorrect");
    }

    @Test
    @DisplayName("Should throw InvalidCredentialsException when password is incorrect")
    public void shouldThrowInvalidCredentialsExceptionWhenPasswordIsIncorrect() {
        User user = VALID_SUPER_ADMIN;
        LoginInput input = new LoginInput(user.getEmail(), user.getPassword());
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        Mockito.when(encoderGateway.matches(input.password(), user.getPassword())).thenReturn(false);
        Assertions.assertThatThrownBy(() -> login.execute(input))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("E-mail or password is incorrect");
    }


    @Test
    @DisplayName("Should throw NotAuthorizedException if user is seller and status is  PENDING")
    public void shouldThrowNotAuthorizedExceptionIfUserIsSellerAndStatusIsPending() {
        User user = VALID_SELLER;
        user.setToPending();

        LoginInput input = new LoginInput(user.getEmail(), user.getPassword());
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        Mockito.when(encoderGateway.matches(input.password(), user.getPassword())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> login.execute(input))
                .isInstanceOf(NotAuthorizedException.class)
                .hasMessage("Your account is not authorized to perform this action, because is in status " + user.getStatus().name()
                        + ". So please contact the administrator so that you can fix it.");
    }

    @Test
    @DisplayName("Should throw NotAuthorizedException if user is seller and status is  SUSPENDED")
    public void shouldThrowNotAuthorizedExceptionIfUserIsSellerAndStatusIsSuspended() {
        User user = VALID_SELLER;
        user.suspend();

        LoginInput input = new LoginInput(user.getEmail(), user.getPassword());
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        Mockito.when(encoderGateway.matches(input.password(), user.getPassword())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> login.execute(input))
                .isInstanceOf(NotAuthorizedException.class)
                .hasMessage("Your account is not authorized to perform this action, because is in status " + user.getStatus().name()
                        + ". So please contact the administrator so that you can fix it.");
    }

    @Test
    @DisplayName("Should throw NotAuthorizedException if user is seller and status is EXPIRED")
    public void shouldThrowNotAuthorizedExceptionIfUserIsSellerAndStatusIsExpired() {
        User user = VALID_SELLER;
        user.setToExpired();

        LoginInput input = new LoginInput(user.getEmail(), user.getPassword());
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        Mockito.when(encoderGateway.matches(input.password(), user.getPassword())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> login.execute(input))
                .isInstanceOf(NotAuthorizedException.class)
                .hasMessage("Your account is not authorized to perform this action, because is in status " + user.getStatus().name()
                        + ". So please contact the administrator so that you can fix it.");
    }

    @Test
    @DisplayName("Should throw NotAuthorizedException if user is seller and status is BLOCKED")
    public void shouldThrowNotAuthorizedExceptionIfUserIsSellerAndStatusIsBlocked() {
        User user = VALID_SELLER;
        user.block();

        LoginInput input = new LoginInput(user.getEmail(), user.getPassword());
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        Mockito.when(encoderGateway.matches(input.password(), user.getPassword())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> login.execute(input))
                .isInstanceOf(NotAuthorizedException.class)
                .hasMessage("Your account is not authorized to perform this action, because is in status " + user.getStatus().name()
                        + ". So please contact the administrator so that you can fix it.");
    }

    @Test
    @DisplayName("Should throw NotAuthorizedException if user is not in status  ACTIVE")
    public void shouldThrowNotAuthorizedExceptionIfUserIsNotInStatusActive() {
        User user = VALID_SUPER_ADMIN;
        user.setToPending();

        LoginInput input = new LoginInput(user.getEmail(), user.getPassword());
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        Mockito.when(encoderGateway.matches(input.password(), user.getPassword())).thenReturn(true);

        Assertions.assertThatThrownBy(() -> login.execute(input))
                .isInstanceOf(NotAuthorizedException.class)
                .hasMessage("Please confirm you account following the instructions sent to your email", "LOGIN-06");
    }

    @Test
    @DisplayName("Should return access accessToken and refresh accessToken on login successs")
    public void shouldReturnAccessTokenAndRefreshTokenOnLoginSuccess() {
        User user = VALID_SUPER_ADMIN;
        String accessToken = "any_access_token";
        String refreshToken = "any_refresh_token";

        LoginInput input = new LoginInput(user.getEmail(), user.getPassword());
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        Mockito.when(encoderGateway.matches(input.password(), user.getPassword())).thenReturn(true);
        Mockito.when(authenticationGateway.authenticate(user)).thenReturn(new AuthenticateOutput(accessToken, refreshToken));
        LoginOutput output = login.execute(input);
        org.junit.jupiter.api.Assertions.assertEquals(accessToken, output.accessToken());
        org.junit.jupiter.api.Assertions.assertEquals(refreshToken, output.refreshToken());
    }
}
