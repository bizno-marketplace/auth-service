package com.biznopay.authservice.presentation.dto;

public record ResubmitSellerRequest(
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
