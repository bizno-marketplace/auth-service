package com.biznopay.authservice.usecase.user.register.buyer;

import com.biznopay.authservice.domain.vo.Address;

public record RegisterBuyerInput(
        String firstName,
        String lastName,
        String email,
        String password,
        String phoneNumber,
        Address address
) {
}
