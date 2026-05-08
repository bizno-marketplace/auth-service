package com.biznopay.authservice.usecase.user.register.sa;

public record RegisterSAInput(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
