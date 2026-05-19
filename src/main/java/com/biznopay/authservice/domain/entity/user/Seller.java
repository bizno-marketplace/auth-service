package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidPhoneNumberException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.BiDocument;
import com.biznopay.authservice.domain.vo.Nuit;

import java.time.LocalDateTime;

public class Seller extends User {
    private static final String MZ_PHONE_REGEX = "^\\+258(8[4-7]|2[1-9])\\d{7}$";

    private final String storeName;
    private final String storeDescription;
    private final Nuit nuit;
    private final Address storeAddress;
    private final BiDocument biDocument;

    private Seller(UserId id, String firstName, String lastname, String email, String phone, String password, UserStatus status,
                   LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt, String storeName,
                   String storeDescription, String nuit, Address storeAddress, BiDocument biDocument) {
        super(id, firstName, lastname, email, phone, password, status, expiresAt, createdAt, updatedAt);
        this.storeName = storeName;
        this.storeDescription = storeDescription;
        this.nuit = new Nuit(nuit);
        this.storeAddress = storeAddress;
        this.biDocument = biDocument;
    }

    public static Seller register(UserId id, String firstName, String lastname, String email, String phone, String password,
                                  String storeName, String storeDescription, String nuit, Address storeAddress, BiDocument biDocument) {
        LocalDateTime now = LocalDateTime.now();
        phone = validatePhone(phone);
        storeName = validateStoreName(storeName);
        storeDescription = validateStoreDescription(storeDescription);
        return new Seller(id, firstName, lastname, email, phone, password, UserStatus.ACTIVE, null, now,
                now, storeName, storeDescription, nuit, storeAddress, biDocument);
    }

    public static Seller reconstitute(UserId id, String firstName, String lastname, String email, String phone, String password,
                                      UserStatus status, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                                      String storeName, String storeDescription, String nuit, Address storeAddress, BiDocument biDocument) {
        phone = validatePhone(phone);
        return new Seller(id, firstName, lastname, email, phone, password, status, expiresAt, createdAt, updatedAt,
                storeName, storeDescription, nuit, storeAddress, biDocument);
    }

    private static String validatePhone(String phone) {
        if (phone == null || phone.isEmpty())
            throw new RequiredFieldException("Phone number", Buyer.class.getName(), "BUYER-001");
        if (!phone.matches(MZ_PHONE_REGEX))
            throw new InvalidPhoneNumberException("BUYER-002");
        return phone;
    }

    private static String validateStoreName(String storeName) {
        if (storeName == null || storeName.isEmpty())
            throw new RequiredFieldException("Store name", Seller.class.getName(), "SELLER-001");
        return storeName;
    }

    private static String validateStoreDescription(String storeDescription) {
        if (storeDescription == null || storeDescription.isEmpty())
            throw new RequiredFieldException("Store description", Seller.class.getName(), "SELLER-002");
        return storeDescription;
    }


    public String getStoreName() {
        return storeName;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public String getNuit() {
        return nuit.value();
    }

    public Address getStoreAddress() {
        return storeAddress;
    }

    public BiDocument getBiDocument() {
        return biDocument;
    }
}
