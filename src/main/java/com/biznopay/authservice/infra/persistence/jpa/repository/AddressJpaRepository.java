package com.biznopay.authservice.infra.persistence.jpa.repository;

import com.biznopay.authservice.infra.persistence.jpa.entity.AddressJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressJpaRepository extends JpaRepository<AddressJpaEntity, Long> {
}
