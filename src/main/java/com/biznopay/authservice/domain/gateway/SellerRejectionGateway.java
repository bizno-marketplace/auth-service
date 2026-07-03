package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.seller.SellerRejection;

import java.util.Optional;
import java.util.UUID;

public interface SellerRejectionGateway {
    Optional<SellerRejection> findByUserId(UUID userId);

    void save(SellerRejection sellerRejection);
}
