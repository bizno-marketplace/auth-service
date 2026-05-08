package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;

public class UserMapper {
    public static UserJpaEntity toUserJpaEntity(User user) {
        return switch (user) {
            case SuperAdmin sa -> toSuperAdminJpaEntity(sa);
            default -> throw new IllegalArgumentException("Unknown user type: " + user.getClass().getName());
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


    public static User toUserDomain(UserJpaEntity entity) {
        return switch (entity) {
            case SuperAdminJpaEntity sa -> toSuperAdminDomainEntity(sa);
            default -> throw new IllegalArgumentException("Unknown user type: " + entity.getClass().getName());
        };
    }

    private static SuperAdmin toSuperAdminDomainEntity(SuperAdminJpaEntity entity) {
        return SuperAdmin.reconstitute(UserId.of(entity.getId()), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPhone(), entity.getPassword(), entity.getStatus(), entity.getExpiresAt(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }
}
