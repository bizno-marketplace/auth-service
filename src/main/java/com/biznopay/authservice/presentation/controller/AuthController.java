package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.presentation.dto.LoginRequest;
import com.biznopay.authservice.presentation.dto.LogoutRequest;
import com.biznopay.authservice.presentation.dto.RefreshTokenRequest;
import com.biznopay.authservice.presentation.dto.ResendConfirmationRequest;
import com.biznopay.authservice.usecase.auth.confirmAccount.ConfirmAccount;
import com.biznopay.authservice.usecase.auth.login.Login;
import com.biznopay.authservice.usecase.auth.login.LoginInput;
import com.biznopay.authservice.usecase.auth.login.LoginOutput;
import com.biznopay.authservice.usecase.auth.logout.Logout;
import com.biznopay.authservice.usecase.auth.logout.LogoutInput;
import com.biznopay.authservice.usecase.auth.logout.LogoutOutput;
import com.biznopay.authservice.usecase.auth.refreshToken.RefreshToken;
import com.biznopay.authservice.usecase.auth.refreshToken.RefreshTokenInput;
import com.biznopay.authservice.usecase.auth.refreshToken.RefreshTokenOutput;
import com.biznopay.authservice.usecase.auth.resendConfirmation.ResendConformation;
import com.biznopay.authservice.usecase.auth.resendConfirmation.ResendConformationOutput;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Auth")
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final ConfirmAccount confirmAccount;
    private final ResendConformation resendConformation;
    private final Login login;
    private final Logout logout;
    private final RefreshToken refreshToken;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody @Valid LoginRequest request) {
        LoginInput input = new LoginInput(request.email(), request.password());
        LoginOutput output = login.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestBody @Valid LogoutRequest request) {
        LogoutInput input = new LogoutInput(request.refreshToken());
        LogoutOutput output = logout.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }

    @GetMapping("/confirm-account")
    public ResponseEntity confirmAccount(@RequestParam(value = "token", required = false) String token) {
        if (token == null || token.isEmpty())
            throw new RequiredFieldException("Token", "AccountController", "ACCOUNT_CONTROLLER-001");
        confirmAccount.execute(token);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<ApiResponse<Object>> resendConfirmation(@RequestBody @Valid ResendConfirmationRequest request) {
        ResendConformationOutput output = resendConformation.execute(request.email());
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Object>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        RefreshTokenInput input = new RefreshTokenInput(request.refreshToken());
        RefreshTokenOutput output = refreshToken.execute(input);
        return ResponseEntity.status(HttpStatus.OK).body(FuncUtils.buildResponseBody(true, output, null));
    }
}
