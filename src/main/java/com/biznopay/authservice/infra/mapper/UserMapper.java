package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.user.*;
import com.biznopay.authservice.domain.exception.UnknownEntityException;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.BiDocument;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;
import com.biznopay.authservice.infra.persistence.jpa.entity.*;
import com.biznopay.authservice.presentation.dto.AddressRequest;
import com.biznopay.authservice.presentation.dto.RegisterBuyerRequest;
import com.biznopay.authservice.presentation.dto.RegisterSARequest;
import com.biznopay.authservice.presentation.dto.RegisterSellerRequest;
import com.biznopay.authservice.usecase.user.register.buyer.RegisterBuyerInput;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSAInput;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSellerInput;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class UserMapper {
    public static UserJpaEntity toUserJpaEntity(User user) {
        return switch (user) {
            case SuperAdmin sa -> toSuperAdminJpaEntity(sa);
            case Buyer buyer -> toBuyerEntity(buyer);
            case Seller seller -> toSellerEntity(seller);
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

    private static BuyerJpaEntity toBuyerEntity(User user) {
        Buyer domain = (Buyer) user;
        AddressJpaEntity address = toAddressJpaEntity(domain.getDeliveryAddress());
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.setId(domain.getId().value());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setStatus(domain.getStatus());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        entity.setPhone(domain.getPhone());
        entity.setDeliveryAddress(address);
        return entity;
    }

    private static SellerJpaEntity toSellerEntity(User user) {
        Seller domain = (Seller) user;
        AddressJpaEntity address = toAddressJpaEntity(domain.getStoreAddress());
        BiDocumentJpaEntity biDocumentJpa = toBiDocJapEntity(domain.getBiDocument());

        SellerJpaEntity entity = new SellerJpaEntity();
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

        entity.setStoreName(domain.getStoreName());
        entity.setStoreDescription(domain.getStoreDescription());
        entity.setNuit(domain.getNuit());
        entity.setStoreAddress(address);
        entity.setBiDocument(biDocumentJpa);
        return entity;
    }


    public static User toUserDomain(UserJpaEntity entity) {
        return switch (entity) {
            case SuperAdminJpaEntity sa -> toSuperAdminDomainEntity(sa);
            case BuyerJpaEntity buyerJpa -> toBuyerDomainEntity(buyerJpa);
            case SellerJpaEntity sellerJpa -> toSellerDomainEntity(sellerJpa);
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

    private static Seller toSellerDomainEntity(SellerJpaEntity entity) {
        Address address = toAddress(entity.getStoreAddress());
        BiDocument biDocument = toDomainBiDoc(entity.getBiDocument());
        return Seller.reconstitute(UserId.of(entity.getId()), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPhone(), entity.getPassword(), entity.getStatus(), entity.getExpiresAt(),
                entity.getCreatedAt(), entity.getUpdatedAt(), entity.getStoreName(), entity.getStoreDescription(), entity.getNuit(), address, biDocument);
    }

    public static RegisterSAInput toRegisterSAInput(RegisterSARequest request) {
        return new RegisterSAInput(request.firstName(), request.lastName(), request.email(), request.password());
    }

    public static RegisterBuyerInput toRegisterBuyerInput(RegisterBuyerRequest request) {
        Address deliveryAddress = toAddress(request.deliveryAddress());
        return new RegisterBuyerInput(request.firstName(), request.lastName(), request.email(), request.password(), request.phoneNumber(), deliveryAddress);
    }

    public static RegisterSellerInput toRegisterSellerInput(RegisterSellerRequest request, MultipartFile biFrontPhoto, MultipartFile biBackPhoto) throws IOException {
        byte[] frontPhotoBytes = biFrontPhoto.getBytes();
        String frontPhotoExt = biFrontPhoto.getOriginalFilename().split("\\.")[1];
        byte[] backPhotoBytes = biBackPhoto.getBytes();
        String backPhotoExt = biBackPhoto.getOriginalFilename().split("\\.")[1];
        BiDocumentRequest biDocument = new BiDocumentRequest(frontPhotoBytes, frontPhotoExt, backPhotoBytes, backPhotoExt);
        Address address = toAddress(request.storeAddress());
        return new RegisterSellerInput(request.firstName(), request.lastName(), request.email(), request.phoneNumber(),
                request.password(), request.storeName(), request.storeDescription(), request.nuit(), address, biDocument);
    }

    public static AddressJpaEntity toAddressJpaEntity(Address address) {
        return new AddressJpaEntity(address.latitude(), address.longitude(), address.street(), address.neighbourhood(),
                address.city(), address.province(), address.country());
    }

    public static Address toAddress(AddressJpaEntity address) {
        return new Address(address.getLatitude(), address.getLatitude(), address.getStreet(), address.getNeighbourhood(),
                address.getCity(), address.getProvince(), address.getCountry());
    }

    public static Address toAddress(AddressRequest request) {
        return new Address(request.latitude(), request.longitude(), request.street(), request.neighbourhood(),
                request.city(), request.province(), request.country());
    }

    public static BiDocumentJpaEntity toBiDocJapEntity(BiDocument biDocument) {
        return new BiDocumentJpaEntity(biDocument.getFrontPath(), biDocument.getBackPath());
    }

    public static BiDocument toDomainBiDoc(BiDocumentJpaEntity biDocument) {
        return BiDocument.of(biDocument.getFrontPath(), biDocument.getBackPath());
    }
}
