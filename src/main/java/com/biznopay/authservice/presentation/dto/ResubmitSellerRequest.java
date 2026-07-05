package com.biznopay.authservice.presentation.dto;

import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;

public record ResubmitSellerRequest (
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
