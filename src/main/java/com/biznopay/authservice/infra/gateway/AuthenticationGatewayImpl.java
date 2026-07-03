package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationGatewayImpl implements AuthenticationGateway {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User loggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        UserJpaEntity userJpaEntity = this.userJpaRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
        return UserMapper.toUserDomain(userJpaEntity);
    }
}
