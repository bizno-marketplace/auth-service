package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {
    private final UserJpaRepository userJpaRepository;

    @Override
    public long countSAs() {
        return userJpaRepository.countSAs();
    }

    @Override
    public void save(User user) {

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }
}
