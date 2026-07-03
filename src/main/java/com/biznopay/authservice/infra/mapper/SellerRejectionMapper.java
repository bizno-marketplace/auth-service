package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.user.seller.SellerRejection;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SellerRejectionJpaEntity;

public class SellerRejectionMapper {
    public static SellerRejection toDomain(SellerRejectionJpaEntity entity) {
        return SellerRejection.reconstruct(entity.getId(), entity.getSeller().getId(), entity.getReasonsForRejections(), entity.getNumberOfRejections());
    }

    public static SellerRejectionJpaEntity toSellerRejectionJpaEntity(SellerRejection sellerRejection) {
        SellerRejectionJpaEntity entity = new SellerRejectionJpaEntity();
        entity.setId(sellerRejection.getId());
        SellerJpaEntity sellerJpa = new SellerJpaEntity();
        sellerJpa.setId(sellerRejection.getUserId().value());
        entity.setSeller(sellerJpa);
        entity.setReasonsForRejections(sellerRejection.getReasonsForRejections());
        entity.setNumberOfRejections(sellerRejection.getNumberOfRejections());
        return entity;
    }
}
