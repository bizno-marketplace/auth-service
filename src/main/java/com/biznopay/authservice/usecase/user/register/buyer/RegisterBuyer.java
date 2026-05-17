package com.biznopay.authservice.usecase.user.register.buyer;

public class RegisterBuyer {
    public RegisterBuyerOutput execute(RegisterBuyerInput input) {
        return new RegisterBuyerOutput("Buyer registered successfully");
    }
}
