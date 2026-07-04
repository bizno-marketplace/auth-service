package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.User;

public interface AuthenticationGateway {
    User loggedUser();
}
