package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidPhoneNumberException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.Address;

import java.time.LocalDateTime;

public class Buyer extends User {
    private static final String MOZ_PHONE_REGEX = "^(\\+258)?(82|83|84|85|86|87)\\d{7}$";
    private Address deliveryAddress;

    private Buyer(UserId id, String firstName, String lastname, String email, String phone, String password, UserStatus status, Address deliveryAddress,
                  LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastname, email, phone, password, status, expiresAt, createdAt, updatedAt);
        this.deliveryAddress = validateAddress(deliveryAddress);
    }

    public static Buyer register(String firstName, String lastname, String email, String phone, String password, Address deliveryAddress) {
        phone = validatePhone(phone);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(2);
        return new Buyer(UserId.generate(), firstName, lastname, email, phone, password, UserStatus.PENDING, deliveryAddress, expiresAt, createdAt, createdAt);
    }

    public static Buyer reconstitute(UserId id, String firstName, String lastName, String email, String phone,
                                     String password, UserStatus status, Address deliveryAddress, LocalDateTime expiresAt,
                                     LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Buyer(id, firstName, lastName, email, phone, password, status, deliveryAddress, expiresAt, createdAt, updatedAt);
    }

    private static String validatePhone(String phone){
        if (phone == null || phone.isEmpty())
            throw new RequiredFieldException("Phone number", Buyer.class.getName(), "BUYER-001");
        if(!phone.matches(MOZ_PHONE_REGEX))
            throw new InvalidPhoneNumberException("BUYER-002");
        return phone;
    }

    private Address validateAddress(Address deliveryAddress) {
        if (deliveryAddress == null)
            throw new RequiredFieldException("Delivery address", Buyer.class.getName(), "BUYER-003");
        return deliveryAddress;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
