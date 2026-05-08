package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;

import java.util.Optional;

public interface UserGateway {
    Optional<SuperAdmin> findSAByEmail(String email);
    void save(User user);
}
