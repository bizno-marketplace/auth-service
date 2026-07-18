package com.biznopay.authservice.usecase.seller.update;

public record UpdateSellerInput(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String storeName,
        String storeDescription
) {
}
