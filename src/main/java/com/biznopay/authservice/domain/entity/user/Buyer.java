package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidPhoneNumberException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Buyer extends User {
    private static final String MOZ_PHONE_REGEX = "^(\\+258)?(82|83|84|85|86|87)\\d{7}$";
    private List<Address> deliveryAddresses = new ArrayList<>();

    private Buyer(UserId id, String firstName, String lastname, String email, String phone, String password, UserStatus status, Address deliveryAddress,
                  LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastname, email, phone, password, Role.BUYER, status, expiresAt, createdAt, updatedAt);
        validateAddress(deliveryAddress);
    }

    private Buyer(UserId id, String firstName, String lastname, String email, String phone, String password, UserStatus status, List<Address> deliveryAddress,
                  LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastname, email, phone, password, Role.BUYER, status, expiresAt, createdAt, updatedAt);
        validateAddressList(deliveryAddress);
    }

    public static Buyer register(String firstName, String lastname, String email, String phone, String password, Address deliveryAddress) {
        phone = validatePhone(phone);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(2);
        return new Buyer(UserId.generate(), firstName, lastname, email, phone, password, UserStatus.PENDING, deliveryAddress, expiresAt, createdAt, createdAt);
    }

    public static Buyer reconstruct(UUID id, String firstName, String lastName, String email, String phone,
                                    String password, UserStatus status, List<Address> deliveryAddress, LocalDateTime expiresAt,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        UserId userId =  UserId.of(id);
        phone = validatePhone(phone);
        return new Buyer(userId, firstName, lastName, email, phone, password, status, deliveryAddress, expiresAt, createdAt, updatedAt);
    }

    private static String validatePhone(String phone) {
        if (phone == null || phone.isEmpty())
            throw new RequiredFieldException("Phone number", Buyer.class.getName(), "BUYER-001");
        if (!phone.matches(MOZ_PHONE_REGEX))
            throw new InvalidPhoneNumberException("BUYER-002");
        return phone;
    }

    private void validateAddress(Address deliveryAddress) {
        if (deliveryAddress == null)
            throw new RequiredFieldException("Delivery address", Buyer.class.getName(), "BUYER-003");
        deliveryAddresses.add(deliveryAddress);
    }

    private void validateAddressList(List<Address> deliveryAddresses) {
        if (deliveryAddresses == null || deliveryAddresses.stream().allMatch(Objects::isNull))
            throw new RequiredFieldException("Delivery address", Buyer.class.getName(), "BUYER-004");
        this.deliveryAddresses = deliveryAddresses;
    }

    public List<Address> getDeliveryAddresses() {
        return deliveryAddresses;
    }
}
