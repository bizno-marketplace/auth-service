package com.biznopay.authservice.usecase.seller.rejectSeller;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.entity.user.seller.SellerRejection;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.InvalidSellerAccountStatus;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.RejectSellerPolicy;
import lombok.RequiredArgsConstructor;


import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class RejectSeller {
    private final TransactionGateway transactionGateway;
    private final AuthenticationGateway authenticationGateway;
    private final RejectSellerPolicy policy;
    private final UserGateway userGateway;
    private final SellerRejectionGateway sellerRejectionGateway;
    private final MetricsGateway metricsGateway;

    public void execute(RejectSellerInput input) {
        transactionGateway.execute(() -> {
            User loggedUser = authenticationGateway.loggedUser();
            policy.enforce(loggedUser, "REJECT_SELLER-001");
            UUID sellerId = validateSellerId(input.sellerId());
            String reason = validateReason(input.reasonForRejection());
            User user = userGateway.findSellerById(sellerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seller", "REJECT_SELLER-004"));
            if (!user.getStatus().equals(UserStatus.AWAITING_APPROVAL))
                throw new InvalidSellerAccountStatus(UserStatus.AWAITING_APPROVAL.name(), "REJECT_SELLER-005");
            SellerRejection sellerRejection = buildSellerRejection(user.getId(), reason);
            saveSellerRejectionAndUser(sellerRejection, reason, user);
            metricsGateway.incrementSellerRejected(reason);
        });
    }

    private UUID validateSellerId(String rawSellerId) {
        try {
            return UUID.fromString(rawSellerId);
        } catch (Exception exception) {
            throw new InvalidFieldException("User Id", "REJECT_SELLER", "REJECT_SELLER-002");
        }
    }

    private String validateReason(String reason) {
        if (reason == null || reason.isEmpty())
            throw new RequiredFieldException("Reason for rejection", "Seller", "APPROVE_SELLER-003");
        return reason;
    }

    private SellerRejection buildSellerRejection(UserId userId, String reasonsForRejections) {
        Optional<SellerRejection> sellerRejectionOpt = sellerRejectionGateway.findByUserId(userId.value());
        return sellerRejectionOpt.orElseGet(() -> SellerRejection.of(userId.value(), reasonsForRejections));
    }

    private void saveSellerRejectionAndUser(SellerRejection sellerRejection, String reason, User user) {
        if (sellerRejection.isBlocked()) {
            user.block();
        } else {
            user.reject();
            sellerRejection.increaseNumberOfAttempts(reason);
        }
        sellerRejectionGateway.save(sellerRejection);
        userGateway.save(user);
    }
}
