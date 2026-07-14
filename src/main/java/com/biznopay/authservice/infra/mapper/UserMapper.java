package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.user.*;
import com.biznopay.authservice.domain.entity.user.seller.Seller;
import com.biznopay.authservice.domain.exception.UnknownEntityException;
import com.biznopay.authservice.domain.vo.BiDocument;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;
import com.biznopay.authservice.grpc.GetUserProfileResponse;
import com.biznopay.authservice.infra.persistence.jpa.entity.*;
import com.biznopay.authservice.presentation.dto.*;
import com.biznopay.authservice.usecase.auth.getUserProfile.GetUserProfileOutput;
import com.biznopay.authservice.usecase.buyer.RegisterBuyerInput;
import com.biznopay.authservice.usecase.sa.RegisterSAInput;
import com.biznopay.authservice.usecase.seller.register.RegisterSellerInput;
import com.biznopay.authservice.usecase.seller.rejectSeller.RejectSellerInput;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSellerInput;
import com.biznopay.authservice.usecase.seller.updateSeller.UpdateSellerInput;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        List<AddressJpaEntity> addresses = toAddressJpaEntityList(domain.getDeliveryAddresses());
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
        entity.setDeliveryAddresses(addresses);
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
        return SuperAdmin.reconstruct(entity.getId(), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPhone(), entity.getPassword(), entity.getStatus(), entity.getExpiresAt(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private static Buyer toBuyerDomainEntity(BuyerJpaEntity entity) {
        List<Address> addresses = toAddressList(entity.getDeliveryAddresses());
        return Buyer.reconstruct(entity.getId(), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPhone(), entity.getPassword(), entity.getStatus(), addresses, entity.getExpiresAt(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private static Seller toSellerDomainEntity(SellerJpaEntity entity) {
        Address address = toAddress(entity.getStoreAddress());
        BiDocument biDocument = toDomainBiDoc(entity.getBiDocument());
        return Seller.reconstruct(UserId.of(entity.getId()), entity.getFirstName(), entity.getLastName(),
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

    public static ResubmitSellerInput toResubmitSellerInput(ResubmitSellerRequest request, MultipartFile biFrontPhoto, MultipartFile biBackPhoto) throws IOException {
        BiDocumentRequest biDocument = null;
        if (biFrontPhoto != null && !biFrontPhoto.isEmpty() && biBackPhoto != null && !biBackPhoto.isEmpty()) {
            byte[] frontPhotoBytes = biFrontPhoto.getBytes();
            String frontPhotoExt = biFrontPhoto.getOriginalFilename().split("\\.")[1];
            byte[] backPhotoBytes = biBackPhoto.getBytes();
            String backPhotoExt = biBackPhoto.getOriginalFilename().split("\\.")[1];
            biDocument = new BiDocumentRequest(frontPhotoBytes, frontPhotoExt, backPhotoBytes, backPhotoExt);
        }

        Address address = request.storeAddress() != null ? toAddress(request.storeAddress()) : null;
        return new ResubmitSellerInput(request.firstName(), request.lastName(), request.email(), request.phoneNumber(),
                request.storeName(), request.storeDescription(), request.nuit(), address, biDocument);
    }

    public static AddressJpaEntity toAddressJpaEntity(Address address) {
        return new AddressJpaEntity(address.getId(), address.getLatitude(), address.getLongitude(), address.getStreet(), address.getNeighbourhood(),
                address.getCity(), address.getProvince(), address.getCountry());
    }

    public static List<AddressJpaEntity> toAddressJpaEntityList(List<Address> addresses) {
        return addresses.stream().map(UserMapper::toAddressJpaEntity).collect(Collectors.toList());
    }

    public static Address toAddress(AddressJpaEntity address) {
        return Address.reconstruct(address.getId(), address.getLatitude(), address.getLatitude(), address.getStreet(), address.getNeighbourhood(),
                address.getCity(), address.getProvince(), address.getCountry());
    }

    public static List<Address> toAddressList(List<AddressJpaEntity> address) {
        return address.stream().map(UserMapper::toAddress).collect(Collectors.toList());
    }

    public static Address toAddress(AddressRequest request) {
        return Address.of(request.latitude(), request.longitude(), request.street(), request.neighbourhood(),
                request.city(), request.province(), request.country());
    }

    public static BiDocumentJpaEntity toBiDocJapEntity(BiDocument biDocument) {
        return new BiDocumentJpaEntity(biDocument.getFrontPath(), biDocument.getBackPath());
    }

    public static BiDocument toDomainBiDoc(BiDocumentJpaEntity biDocument) {
        return BiDocument.of(biDocument.getFrontPath(), biDocument.getBackPath());
    }

    public static RejectSellerInput toRejectSellerInput(String sellerId, RejectSellerRequest request) {
        return new RejectSellerInput(sellerId, request.reasonForRejection());
    }

    public static GetUserProfileResponse toGetUserProfileResponse(GetUserProfileOutput output) {
        return GetUserProfileResponse.newBuilder()
                .setUserId(output.userId())
                .setEmail(output.email())
                .setFirstName(output.firstName())
                .setLastName(output.lastName())
                .setRole(output.role())
                .setStatus(output.status())
                .build();
    }

    public static UpdateSellerInput toUpdateSellerInput(UpdateSellerRequest request) {
        return new UpdateSellerInput(request.firstName(), request.lastName(), request.email(), request.phoneNumber(),
                request.storeName(), request.storeDescription());
    }
}
