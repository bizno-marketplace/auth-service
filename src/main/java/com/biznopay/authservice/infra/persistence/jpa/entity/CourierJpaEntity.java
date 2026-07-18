package com.biznopay.authservice.infra.persistence.jpa.entity;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.enums.VehicleTypeEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "T_COURIERS")
@DiscriminatorValue("COURIER")
public class CourierJpaEntity extends UserJpaEntity {
    private VehicleTypeEnum vehicleType;
    private String licenseNumber;
    private String zone;

    public CourierJpaEntity(UUID id, String firstName, String lastName, String email, String phone, String password,
                            VehicleTypeEnum vehicleType, String licenseNumber, String zone, UserStatus status,
                            LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.zone = zone;
    }

    public CourierJpaEntity() {
    }
}
