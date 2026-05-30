package com.biznopay.authservice.usecase.user.register.buyer;

import com.biznopay.authservice.domain.entity.user.Address;

public record RegisterBuyerInput(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,
        Address address
) {
}
