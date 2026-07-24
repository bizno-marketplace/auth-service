package com.biznopay.authservice.usecase.auth.login;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidCredentialsException;
import com.biznopay.authservice.domain.exception.NotAuthorizedException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.EncoderGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.vo.AuthenticateOutput;

public class Login {
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final AuthenticationGateway authenticationGateway;

    public Login(UserGateway userGateway, EncoderGateway encoderGateway, AuthenticationGateway authenticationGateway) {
        this.userGateway = userGateway;
        this.encoderGateway = encoderGateway;
        this.authenticationGateway = authenticationGateway;
    }

    public LoginOutput execute(LoginInput input) {
        validLoginInput(input);
        User user = userGateway.findByEmail(input.email())
                .orElseThrow(() -> new InvalidCredentialsException("LOGIN-003"));
        boolean isPasswordValid = encoderGateway.matches(input.password(), user.getPassword());
        if (!isPasswordValid) throw new InvalidCredentialsException("LOGIN-004");
        validateUserStatus(user);
        AuthenticateOutput output = authenticationGateway.authenticate(user);
        return new LoginOutput(output.token(), output.refreshToken());
    }

    private void validLoginInput(LoginInput input) {
        if (input.email() == null || input.email().isEmpty())
            throw new RequiredFieldException("E-mail", "Login", "LOGIN-001");
        if (input.password() == null || input.password().isEmpty())
            throw new RequiredFieldException("Password", "Login", "LOGIN-002");
    }

    private void validateUserStatus(User user) {
        if (user.getRole().equals(Role.SELLER) &&
                (user.getStatus().equals(UserStatus.PENDING)
                        || user.getStatus().equals(UserStatus.SUSPENDED)
                        || user.getStatus().equals(UserStatus.EXPIRED)
                        || user.getStatus().equals(UserStatus.BLOCKED))) {
            throw new NotAuthorizedException(
                    "Your account is not authorized to perform this action, because is in status " + user.getStatus().name()
                            + ". So please contact the administrator so that you can fix it."
                    , "LOGIN-05");
        } else if (!user.getStatus().equals(UserStatus.ACTIVE)) {
            throw new NotAuthorizedException("Please confirm you account following the instructions sent to your email", "LOGIN-06");
        }
    }
}
