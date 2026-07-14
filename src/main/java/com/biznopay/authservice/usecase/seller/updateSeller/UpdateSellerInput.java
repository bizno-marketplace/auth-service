package com.biznopay.authservice.usecase.seller.updateSeller;

public record UpdateSellerInput(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String storeName,
        String storeDescription
) {
}
