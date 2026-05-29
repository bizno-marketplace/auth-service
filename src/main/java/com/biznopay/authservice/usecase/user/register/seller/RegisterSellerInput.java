package com.biznopay.authservice.usecase.user.register.seller;

import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;

public record RegisterSellerInput(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String password,
        String storeName,
        String storeDescription,
        String nuit,
        Address storeAddress,
        BiDocumentRequest biDocument
) {
}