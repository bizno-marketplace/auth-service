package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.User;

import java.util.Optional;

public interface UserGateway {
    long countSAs();
    void save(User user);
    Optional<User> findByEmail(String email);
}
