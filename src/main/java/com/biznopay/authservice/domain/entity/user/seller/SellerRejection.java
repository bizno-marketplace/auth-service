package com.biznopay.authservice.domain.entity.user.seller;

import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.BlockedAccountException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SellerRejection {
    private final Long id;
    private final UserId userId;
    private List<String> reasonsForRejections;
    private int numberOfRejections;

    private SellerRejection(Long id, UserId userId, List<String> reasonForRejection, int numberOfRejections) {
        this.id = id;
        this.userId = userId;
        this.reasonsForRejections = reasonForRejection;
        this.numberOfRejections = numberOfRejections;
    }

    public static SellerRejection of(UUID rawUserId, String reasonForRejection) {
        UserId userId = UserId.of(rawUserId);
        List<String> reasonsForRejections = new ArrayList<>();
        reasonsForRejections.add(reasonForRejection);
        return new SellerRejection(null, userId, reasonsForRejections, 0);
    }

    public static SellerRejection reconstruct(Long id, UUID rawUserId, List<String> reasonForRejection, int numberOfRejections) {
        UserId userId = UserId.of(rawUserId);
        return new SellerRejection(id, userId, reasonForRejection, numberOfRejections);
    }

    public void increaseNumberOfAttempts(String reasonForRejection) {
        if (numberOfRejections >= 3) throw new BlockedAccountException("SELLER_REJECTION-001");
        reasonsForRejections.add(reasonForRejection);
        numberOfRejections = numberOfRejections + 1;
    }

    public boolean isBlocked() {
        return numberOfRejections >= 3;
    }

    public Long getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public List<String> getReasonsForRejections() {
        return reasonsForRejections;
    }

    public int getNumberOfRejections() {
        return numberOfRejections;
    }
}
