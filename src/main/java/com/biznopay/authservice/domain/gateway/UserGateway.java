package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserGateway {
    long countSAs();

    void save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID userId);

    Optional<User> findByNuit(String nuit);

    Optional<User> findSellerById(UUID id);

    void update(User user);

}
