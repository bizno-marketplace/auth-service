package com.biznopay.authservice.infra.dto;

import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;

public record RegisterSellerRequest(
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
