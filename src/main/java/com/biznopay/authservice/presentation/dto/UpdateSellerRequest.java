package com.biznopay.authservice.presentation.dto;

public record UpdateSellerRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String storeName,
        String storeDescription
) {
}
