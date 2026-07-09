package com.biznopay.authservice.usecase.seller.approveSeller;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.InvalidSellerAccountStatus;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.MetricsGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.policy.ApproveSellerPolicy;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class ApproveSeller {
    private final ApproveSellerPolicy policy;
    private final AuthenticationGateway authenticationGateway;
    private final UserGateway userGateway;
    private final MetricsGateway metricsGateway;

    public void execute(ApproveSellerInput input) {
        User loggedUser = authenticationGateway.loggedUser();
        policy.enforce(loggedUser, "APPROVE_SELLER-001");
        UUID sellerId = validateSellerId(input.sellerId());
        User user = userGateway.findSellerById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "APPROVE_SELLER-003"));
        if (!user.getStatus().equals(UserStatus.AWAITING_APPROVAL))
            throw new InvalidSellerAccountStatus(UserStatus.AWAITING_APPROVAL.name(), "APPROVE_SELLER-004");
        user.activate();
        userGateway.save(user);
        metricsGateway.incrementSellerApproved();
    }

    private UUID validateSellerId(String rawSellerId) {
        UUID sellerId;
        try {
            sellerId = UUID.fromString(rawSellerId);
        } catch (Exception exception) {
            throw new InvalidFieldException("User Id", "APPROVE_SELLER", "APPROVE_SELLER-002");
        }
        return sellerId;
    }
}