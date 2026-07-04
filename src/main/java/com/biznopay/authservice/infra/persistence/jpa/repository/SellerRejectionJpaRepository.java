package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.infra.persistence.jpa.entity.SellerRejectionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SellerRejectionJpaRepository extends JpaRepository<SellerRejectionJpaEntity, Long> {
    Optional<SellerRejectionJpaEntity> findBySellerId(UUID sellerId);
}
