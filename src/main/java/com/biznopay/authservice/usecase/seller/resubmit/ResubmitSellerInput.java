package com.biznopay.authservice.usecase.seller.resubmit;

import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;

public record ResubmitSellerInput(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        String storeName,
        String storeDescription,
        String nuit,
        Address storeAddress,
        BiDocumentRequest biDocument
) {
}