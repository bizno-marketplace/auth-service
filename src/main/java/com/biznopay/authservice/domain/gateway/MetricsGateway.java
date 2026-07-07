package com.biznopay.authservice.domain.gateway;

public interface MetricsGateway {
    // Seller
    void incrementSellerRegistered();
    void incrementSellerApproved();
    void incrementSellerRejected(String reason);
    void incrementSellerResubmitted();

    // Buyer
    void incrementBuyerRegistered();

    // Auth
    void recordValidateTokenDuration(long durationMillis);
}
