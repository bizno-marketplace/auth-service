package com.biznopay.authservice.domain.policy;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.domain.exception.AccessDeniedException;

public class RegisterCourierPolicy {
    public void enforce(User requestingUser, String code) {
        if (requestingUser == null || requestingUser.getRole() != Role.SUPER_ADMIN) {
            throw new AccessDeniedException(code);
        }
    }
}