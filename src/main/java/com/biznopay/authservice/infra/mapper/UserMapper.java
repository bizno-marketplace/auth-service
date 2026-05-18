package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.UnknownEntityException;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.infra.dto.AddressRequest;
import com.biznopay.authservice.infra.dto.RegisterBuyerRequest;
import com.biznopay.authservice.infra.dto.RegisterSARequest;
import com.biznopay.authservice.infra.persistence.jpa.entity.AddressJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.BuyerJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.SuperAdminJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.usecase.user.register.buyer.RegisterBuyerInput;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAInput;

public class UserMapper {
    public static UserJpaEntity toUserJpaEntity(User user) {
        return switch (user) {
            case SuperAdmin sa -> toSuperAdminJpaEntity(sa);
            case Buyer buyer -> toBuyerEntity(buyer);
            default ->
                    throw new UnknownEntityException("Unknown entity: " + user.getClass().getName(), "USER_MAPPER-0001");
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

    public static AddressJpaEntity toAddressJpaEntity(Address address) {
        return new AddressJpaEntity(address.latitude(), address.longitude(), address.street(), address.neighbourhood(),
                address.city(), address.province(), address.country());
    }

    public static Address toAddress(AddressJpaEntity address) {
        return new Address(address.getLatitude(), address.getLatitude(), address.getStreet(), address.getNeighbourhood(),
                address.getCity(), address.getProvince(), address.getCountry());
    }


    private static BuyerJpaEntity toBuyerEntity(User user) {
        Buyer domain = (Buyer) user;
        AddressJpaEntity address = toAddressJpaEntity(domain.getDeliveryAddress());
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setId(domain.getId().value());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setPassword(domain.getPassword());
        entity.setStatus(domain.getStatus());
        entity.setDeliveryAddress(address);
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }


    public static User toUserDomain(UserJpaEntity entity) {
        return switch (entity) {
            case SuperAdminJpaEntity sa -> toSuperAdminDomainEntity(sa);
            case BuyerJpaEntity buyerJpa -> toBuyerDomainEntity(buyerJpa);
            default ->
                    throw new UnknownEntityException("Unknown entity: " + entity.getClass().getName(), "USER_MAPPER-0002S");
        };
    }

    private static SuperAdmin toSuperAdminDomainEntity(SuperAdminJpaEntity entity) {
        return SuperAdmin.reconstitute(UserId.of(entity.getId()), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPhone(), entity.getPassword(), entity.getStatus(), entity.getExpiresAt(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private static Buyer toBuyerDomainEntity(BuyerJpaEntity entity) {
        Address address = toAddress(entity.getDeliveryAddress());
        return Buyer.reconstitute(UserId.of(entity.getId()), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPhone(), entity.getPassword(), entity.getStatus(), address, entity.getExpiresAt(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    public static RegisterSAInput toRegisterSAInput(RegisterSARequest request) {
        return new RegisterSAInput(request.firstName(), request.lastName(), request.email(), request.password());
    }

    public static Address toAddress(AddressRequest request) {
        return new Address(request.latitude(), request.longitude(), request.street(), request.neighbourhood(),
                request.city(), request.province(), request.country());
    }

    public static RegisterBuyerInput toRegisterBuyerInput(RegisterBuyerRequest request) {
        Address deliveryAddress = toAddress(request.deliveryAddress());
        return new RegisterBuyerInput(request.firstName(), request.lastName(), request.email(), request.password(), request.phoneNumber(), deliveryAddress);
    }
}
