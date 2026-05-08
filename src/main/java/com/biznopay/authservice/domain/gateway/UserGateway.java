package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;

import java.util.Optional;

public interface UserGateway {
    Optional<SuperAdmin> findSuperByEmail(String email);
}
