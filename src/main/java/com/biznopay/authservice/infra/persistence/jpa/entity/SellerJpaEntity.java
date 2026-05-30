package com.biznopay.authservice.infra.persistence.jpa.entity;

import com.biznopay.authservice.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "T_SELLERS")
@DiscriminatorValue("SELLER")
public class SellerJpaEntity extends UserJpaEntity {
    private String storeName;
    private String storeDescription;
    private String nuit;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_address_id", nullable = false)
    private AddressJpaEntity storeAddress;

    @Embedded
    private BiDocumentJpaEntity biDocument;

    public SellerJpaEntity(UUID id, String firstName, String lastName, String email, String phone, String password,
                           UserStatus status, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                           String storeName, String storeDescription, String nuit, AddressJpaEntity storeAddress, BiDocumentJpaEntity biDocument) {
        super(id, firstName, lastName, email, phone, password, status, expiresAt, createdAt, updatedAt);
        this.storeName = storeName;
        this.storeDescription = storeDescription;
        this.nuit = nuit;
        this.storeAddress = storeAddress;
        this.biDocument = biDocument;
    }

    public SellerJpaEntity() {
    }
}
