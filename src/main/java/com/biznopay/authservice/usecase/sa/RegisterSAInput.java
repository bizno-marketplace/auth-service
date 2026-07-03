package com.biznopay.authservice.usecase.sa;

public record RegisterSAInput(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
