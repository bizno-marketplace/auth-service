package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.SellerJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.SuperAdminJpaRepository;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserGatewayImpl implements UserGateway {
    private final UserJpaRepository userJpaRepository;
    private final SuperAdminJpaRepository superAdminJpaRepository;
    private final SellerJpaRepository sellerJpaRepository;

    @Override
    public long countSAs() {
        return superAdminJpaRepository.countBy();
    }

    @Override
    public void save(User user) {
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        userJpaRepository.save(entity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<UserJpaEntity> entity = userJpaRepository.findByEmail(email);
        return entity.map(UserMapper::toUserDomain);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        Optional<UserJpaEntity> entity = userJpaRepository.findById(userId);
        return entity.map(UserMapper::toUserDomain);
    }

    @Override
    public Optional<User> findByNuit(String nuit) {
        Optional<UserJpaEntity> entity = sellerJpaRepository.findByNuit(nuit);
        return entity.map(UserMapper::toUserDomain);
    }

    @Override
    public Optional<User> findSellerById(UUID id) {
        Optional<SellerJpaEntity> userOpt = sellerJpaRepository.findById(id);
        return userOpt.map(UserMapper::toUserDomain);
    }

    public UserDetails findByUsername(String username){
        return this.userJpaRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found!"));
    }
}
