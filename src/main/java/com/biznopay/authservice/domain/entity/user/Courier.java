package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.Role;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.enums.VehicleTypeEnum;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Courier extends User {
    private final VehicleTypeEnum vehicleType;
    private final String licenseNumber;
    private final String zone;

    private Courier(UserId id, String firstName, String lastname, String email, String phone, String password,
                    VehicleTypeEnum vehicleType, String licenseNumber, String zone, LocalDateTime expiresAt,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastname, email, phone, password, Role.COURIER, UserStatus.PENDING, expiresAt, createdAt, updatedAt);

        this.vehicleType = validateVehicleType(vehicleType);
        this.licenseNumber = validaLicenseNumber(licenseNumber);
        this.zone = this.validateZone(zone);
    }

    public static Courier register(String firstName, String lastname, String email, String phone, String password,
                                   VehicleTypeEnum vehicleType, String licenseNumber, String zone) {
        phone = validatePhone(phone);
        LocalDateTime creationDateTime = LocalDateTime.now();
        return new Courier(UserId.generate(), firstName, lastname, email, phone, password, vehicleType, licenseNumber,
                zone, null, creationDateTime, creationDateTime);
    }

    public static Courier reconstruct(UUID id, String firstName, String lastname, String email, String phone, String password,
                                      VehicleTypeEnum vehicleType, String licenseNumber, String zone, LocalDateTime expiresAt,
                                      LocalDateTime createdAt, LocalDateTime updatedAt) {

        phone = validatePhone(phone);
        return new Courier(UserId.of(id), firstName, lastname, email, phone, password, vehicleType, licenseNumber, zone, expiresAt, createdAt, updatedAt);
    }

    private static String validatePhone(String phone) {
        if (phone == null || phone.isEmpty())
            throw new RequiredFieldException("Phone number", "Courier", "COURIER-001");
        return phone;
    }

    private VehicleTypeEnum validateVehicleType(VehicleTypeEnum vehicleType) {
        if (vehicleType == null)
            throw new RequiredFieldException("Vehicle type", "Courier", "COURIER-002");
        return vehicleType;
    }

    private String validaLicenseNumber(String licenseNumber) {
        if (licenseNumber == null || licenseNumber.isEmpty())
            throw new RequiredFieldException("License number", "Courier", "COURIER-003");
        return licenseNumber;
    }

    private String validateZone(String zone) {
        if (zone == null || zone.isEmpty())
            throw new RequiredFieldException("Zone", "Courier", "COURIER-004");
        return zone;
    }

    public VehicleTypeEnum getVehicleType() {
        return vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getZone() {
        return zone;
    }
}
