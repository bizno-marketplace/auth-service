package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.user.seller.SellerRejection;
import com.biznopay.authservice.domain.gateway.SellerRejectionGateway;
import com.biznopay.authservice.infra.mapper.SellerRejectionMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerRejectionJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.SellerRejectionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SellerRejectionGatewayImpl implements SellerRejectionGateway {
    private final SellerRejectionJpaRepository sellerRejectionJpaRepository;

    @Override
    public Optional<SellerRejection> findByUserId(UUID userId) {
        Optional<SellerRejectionJpaEntity> sellerRejectionJpa = sellerRejectionJpaRepository.findBySellerId(userId);
        return sellerRejectionJpa.map(SellerRejectionMapper::toDomain);
    }

    @Override
    public void save(SellerRejection sellerRejection) {
        SellerRejectionJpaEntity entity = SellerRejectionMapper.toSellerRejectionJpaEntity(sellerRejection);
        sellerRejectionJpaRepository.save(entity);
    }
}
