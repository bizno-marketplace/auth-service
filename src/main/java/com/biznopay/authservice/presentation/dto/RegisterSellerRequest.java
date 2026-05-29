package com.biznopay.authservice.presentation.dto;

public record RegisterSellerRequest(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String password,
        String storeName,
        String storeDescription,
        String nuit,
        AddressRequest storeAddress
) {
}
