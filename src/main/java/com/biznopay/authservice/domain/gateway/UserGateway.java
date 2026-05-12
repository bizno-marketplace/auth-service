package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;

import java.util.Optional;
import java.util.UUID;

public interface UserGateway {
    long countSAs();

    void save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID userId);
}
