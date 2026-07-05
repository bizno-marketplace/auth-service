package com.biznopay.authservice.domain.policy;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccessDeniedException;

public class ResubmitSellerPolicy {

    public void enforce(User requestingUser, String code) {
        if (requestingUser.getRole() != Role.SELLER
                || !requestingUser.getStatus().equals(UserStatus.REJECTED)) {
            throw new AccessDeniedException(code);
        }
    }
}