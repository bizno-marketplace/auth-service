package com.biznopay.authservice.domain.policy;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccessDeniedException;

public class UpdateSellerPolicy {
    public void enforce(User seller, String code) {
        if (seller == null
                || seller.getRole() != Role.SELLER
                || seller.getStatus() == UserStatus.REJECTED
                || seller.getStatus() == UserStatus.SUSPENDED
                || seller.getStatus() == UserStatus.EXPIRED
                || seller.getStatus() == UserStatus.BLOCKED) {
            throw new AccessDeniedException(code);
        }
    }
}
