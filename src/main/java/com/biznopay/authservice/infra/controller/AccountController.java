package com.biznopay.authservice.infra.controller;

import com.biznopay.authservice.usecase.user.confirmAccount.ConfirmAccount;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Tag(name = "Accounts")
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {
    private final ConfirmAccount confirmAccount;

    @GetMapping
    public ResponseEntity confirmAccount(@RequestParam("token") String token) {
        UUID tokenId = UUID.fromString(token);
        confirmAccount.execute(tokenId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
