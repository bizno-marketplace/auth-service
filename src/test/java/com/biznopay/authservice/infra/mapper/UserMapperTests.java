package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserMapperTests {
    @Test
    @DisplayName("Should return Super admin jpa entity on toUserJpaEntity")
    public void shouldReturnSuperAdminJpaEntityOnToUserJpaEntity() {
        User user = SuperAdmin.register("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        Assertions.assertInstanceOf(SuperAdminJpaEntity.class, entity);
        Assertions.assertEquals(user.getId().value(), entity.getId());
        Assertions.assertEquals(user.getFirstName(), entity.getFirstName());
        Assertions.assertEquals(user.getLastName(), entity.getLastName());
        Assertions.assertEquals(user.getEmail(), entity.getEmail());
        Assertions.assertEquals("", entity.getPhone());
        Assertions.assertEquals(user.getPassword(), entity.getPassword());
        Assertions.assertEquals(user.getStatus(), entity.getStatus());
        Assertions.assertEquals(user.getExpiresAt(), entity.getExpiresAt());
        Assertions.assertEquals(user.getCreatedAt(), entity.getCreatedAt());
        Assertions.assertEquals(user.getUpdatedAt(), entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return Buyer jpa entity on toUserJpaEntity")
    public void shouldReturnBuyerJpaEntityOnToUserJpaEntity(){
        User user = Buyer.register("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        UserJpaEntity entity = UserMapper.toUserJpaEntity(user);
        Assertions.assertInstanceOf(UserJpaEntity.class, entity);
        Assertions.assertEquals(user.getId().value(), entity.getId());
        Assertions.assertEquals(user.getFirstName(), entity.getFirstName());
        Assertions.assertEquals(user.getLastName(), entity.getLastName());
        Assertions.assertEquals(user.getEmail(), entity.getEmail());
        Assertions.assertEquals("", entity.getPhone());
        Assertions.assertEquals(user.getPassword(), entity.getPassword());
        Assertions.assertEquals(user.getStatus(), entity.getStatus());
        Assertions.assertEquals(user.getExpiresAt(), entity.getExpiresAt());
        Assertions.assertEquals(user.getCreatedAt(), entity.getCreatedAt());
        Assertions.assertEquals(user.getUpdatedAt(), entity.getUpdatedAt());
    }
}
