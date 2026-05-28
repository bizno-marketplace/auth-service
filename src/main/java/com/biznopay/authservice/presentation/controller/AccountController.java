package com.biznopay.authservice.presentation.controller;

import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.ApiResponse;
import com.biznopay.authservice.infra.util.FuncUtils;
import com.biznopay.authservice.presentation.dto.ResendConfirmationRequest;
import com.biznopay.authservice.usecase.user.account.confirmAccount.ConfirmAccount;
import com.biznopay.authservice.usecase.user.account.resendConfirmation.ResendConformation;
import com.biznopay.authservice.usecase.user.account.resendConfirmation.ResendConformationOutput;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Accounts")
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final ConfirmAccount confirmAccount;
    private final ResendConformation resendConformation;

    @GetMapping("/confirm-account")
    public ResponseEntity confirmAccount(@RequestParam("token") String token) {
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
}
