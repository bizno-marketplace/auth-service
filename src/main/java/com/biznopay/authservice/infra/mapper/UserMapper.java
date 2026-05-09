package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.UnknownEntityException;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.persistence.jpa.entity.BuyerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAInput;

public class UserMapper {
    public static UserJpaEntity toUserJpaEntity(User user) {
        return switch (user) {
            case SuperAdmin sa -> toSuperAdminJpaEntity(sa);
            case Buyer buyer -> toBuyerEntity(buyer);
            default -> throw new UnknownEntityException("Unknown entity: " + user.getClass().getName(), "USER_MAPPER-0001");
        };
    }

    private static UserJpaEntity toSuperAdminJpaEntity(User domain) {
        SuperAdminJpaEntity entity = new SuperAdminJpaEntity();
        entity.setId(domain.getId().value());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setPassword(domain.getPassword());
        entity.setStatus(domain.getStatus());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private static BuyerJpaEntity toBuyerEntity(User domain) {
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setId(domain.getId().value());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setPassword(domain.getPassword());
        entity.setStatus(domain.getStatus());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }


    public static User toUserDomain(UserJpaEntity entity) {
        return switch (entity) {
            case SuperAdminJpaEntity sa -> toSuperAdminDomainEntity(sa);
            case BuyerJpaEntity buyerJpa -> toBuyerDomainEntity(buyerJpa);
            default -> throw new IllegalArgumentException("Unknown user type: " + entity.getClass().getName());
        };
    }

    private static SuperAdmin toSuperAdminDomainEntity(SuperAdminJpaEntity entity) {
        return SuperAdmin.reconstitute(UserId.of(entity.getId()), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPhone(), entity.getPassword(), entity.getStatus(), entity.getExpiresAt(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private static Buyer toBuyerDomainEntity(BuyerJpaEntity entity) {
        return Buyer.reconstitute(UserId.of(entity.getId()), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPhone(), entity.getPassword(), entity.getStatus(), entity.getExpiresAt(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public static RegisterSAInput toRegisterSAInput(RegisterSARequest request) {
        return new RegisterSAInput(request.firstName(), request.lastName(), request.email(), request.password());
    }
}
